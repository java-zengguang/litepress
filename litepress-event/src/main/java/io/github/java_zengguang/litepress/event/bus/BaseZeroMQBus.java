package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.entity.ZoreMQConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import org.tinylog.Logger;
import org.zeromq.ZMQ;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class BaseZeroMQBus extends BaseMessageBus implements MessageBus {

    private final ZoreMQConfig config;
    private final ZMQ.Context context;
    private final ZMQ.Socket publisher;
    private final ZMQ.Socket subscriber;
    private final ExecutorService[] workers;
    private Thread receiverThread;

    public BaseZeroMQBus(ZoreMQConfig config) {
        this.config = config;
        try {
            this.context = ZMQ.context(config.ioThreads);
            this.publisher = context.socket(ZMQ.PAIR);
            this.subscriber = context.socket(ZMQ.PAIR);
            publisher.setHWM(config.hwm);
            subscriber.setHWM(config.hwm);
            publisher.bind(config.busAddress);
            subscriber.connect(config.busAddress);
            this.workers = new ExecutorService[config.workerThreads];
            for (int i = 0; i < workers.length; i++) {
                workers[i] = Executors.newSingleThreadExecutor();
            }
            startReceiver();
        } catch (Exception e) {
            cleanup();
            throw e;
        }
    }

    public BaseZeroMQBus() {
        this(new ZoreMQConfig());
    }

    private void startReceiver() {
        receiverThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 第一帧: sessionId，第二帧: 消息体
                    String sessionId = subscriber.recvStr(0);
                    if (sessionId == null) {
                        continue;
                    }
                    String message = subscriber.recvStr(0);
                    if (message == null) {
                        Logger.warn("收到sessionId但message为null, sessionId={}", sessionId);
                        continue;
                    }
                    submitBySession(sessionId, message);
                } catch (Exception e) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    Logger.error(e, "zeromq接收线程异常, 继续运行");
                }
            }
            Logger.info("zeromq接收线程退出");
        }, "zeromq-bus-receiver");
        receiverThread.start();
    }

    // sessionId hash路由到固定worker，同session串行，不同session并行
    private void submitBySession(String sessionId, String message) {
        int index = (sessionId.hashCode() & Integer.MAX_VALUE) % workers.length;
        workers[index].submit(() -> doInvokeEventListener(message));
    }

    public void shutdown() {
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
        for (ExecutorService worker : workers) {
            worker.shutdown();
        }
        for (ExecutorService worker : workers) {
            try {
                worker.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                worker.shutdownNow();
            }
        }
        publisher.close();
        subscriber.close();
        context.term();
    }

    // 构造器失败时清理已创建的资源，避免泄漏
    private void cleanup() {
        for (ExecutorService worker : workers) {
            if (worker != null) {
                worker.shutdownNow();
            }
        }
        if (publisher != null) {
            publisher.close();
        }
        if (subscriber != null) {
            subscriber.close();
        }
        if (context != null) {
            context.term();
        }
    }

    @Override
    public void doPublish(BaseEvent baseEvent) {
        if (baseEvent.accessId == null) {
            Logger.error("事件accessId为null, 无法路由, event={}", baseEvent.name);
            return;
        }
        // 第一帧: sessionId 用于路由，第二帧: 消息体
        if (!publisher.sendMore(baseEvent.accessId)) {
            Logger.error("发送sessionId失败, event={}", baseEvent.name);
            return;
        }
        if (!publisher.send(JsonUtil.obj2String(baseEvent))) {
            Logger.error("发送事件失败, event={}", baseEvent.name);
        }
    }

    @Override
    public void doSubscriber(String eventType, BaseEventListener listener)  {
        eventListenerMap.putIfAbsent(eventType, listener);
    }

}