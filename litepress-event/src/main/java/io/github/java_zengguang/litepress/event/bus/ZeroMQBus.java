package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.entity.ZoreMQConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import org.tinylog.Logger;
import org.zeromq.ZMQ;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ZeroMQBus extends BaseMessageBus implements MessageBus {

    private final ZMQ.Context context;
    private final ZMQ.Socket publisher;
    private final ZMQ.Socket subscriber;
    private final ThreadPoolExecutor workerPool;
    private Thread receiverThread;

    public ZeroMQBus(ZoreMQConfig config) {
        try {
            this.context = ZMQ.context(config.ioThreads);
            this.publisher = context.socket(ZMQ.PAIR);
            this.subscriber = context.socket(ZMQ.PAIR);
            publisher.setHWM(config.hwm);
            subscriber.setHWM(config.hwm);
            publisher.bind(config.busAddress);
            subscriber.connect(config.busAddress);
            this.workerPool = new ThreadPoolExecutor(
                    config.workerThreads, config.workerThreads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(config.hwm),
                    r -> new Thread(r, "zeromq-bus-worker"),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            startReceiver();
        } catch (Exception e) {
            cleanup();
            throw e;
        }
    }

    public ZeroMQBus() {
        this(new ZoreMQConfig());
    }

    private void startReceiver() {
        receiverThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String message = subscriber.recvStr(0);
                    if (message != null) {
                        workerPool.submit(() -> doInvokeEventListener(message));
                    }
                } catch (Exception e) {
                    if (Thread.currentThread().isInterrupted()) break;
                    Logger.error(e, "zeromq接收线程异常, 继续运行");
                }
            }
            Logger.info("zeromq接收线程退出");
        }, "zeromq-bus-receiver");
        receiverThread.start();
    }

    public void shutdown() {
        receiverThread.interrupt();
        workerPool.shutdown();
        try {
            workerPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
        }
        publisher.close();
        subscriber.close();
        context.term();
    }

    private void cleanup() {
        workerPool.shutdownNow();
        publisher.close();
        subscriber.close();
        context.term();
    }

    @Override
    public void doPublish(BaseEvent baseEvent) {
        if (!publisher.send(JsonUtil.obj2String(baseEvent))) {
            Logger.error("发送事件失败, event={}", baseEvent.name);
        }
    }

    @Override
    public void doSubscriber(String eventType, BaseEventListener listener) {
        eventListenerMap.putIfAbsent(eventType, listener);
    }

}