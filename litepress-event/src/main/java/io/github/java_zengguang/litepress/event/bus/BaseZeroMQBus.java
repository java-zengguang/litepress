package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.core.error.BizException;
import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.entity.ZoreMQConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.tinylog.Logger;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseZeroMQBus extends BaseMessageBus implements MessageBus {


    private final ZoreMQConfig config;

    private ZMQ.Socket publisher;

    private ZMQ.Socket subscriber;

    private final ConcurrentHashMap<String, ExecutorService> executors = new ConcurrentHashMap<>();

    private Thread receiverThread;


    public BaseZeroMQBus(ZoreMQConfig config) {
        this.config = config;
        ZMQ.Context context = ZMQ.context(config.ioThreads);
        // 使用PAIR模式
        publisher = context.socket(ZMQ.PAIR);
        subscriber = context.socket(ZMQ.PAIR);
        publisher.bind(config.busAddress);
        subscriber.connect(config.busAddress);
    }

    public BaseZeroMQBus() {
        this(new ZoreMQConfig());
    }


    // 按用户/会话ID分组，相同ID的顺序处理

    public void init() {
        receiverThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                String topic = subscriber.recvStr(0);
                String message = subscriber.recvStr(0);
                BaseEvent baseEvent = JsonUtil.string2Obj(message, BaseEvent.class);
                String sessionId = baseEvent.accessId; // 提取会话ID

                executors.computeIfAbsent(sessionId, k -> {
                    if (executors.size() >= config.maxSessionThreads) {
                        Logger.warn("会话线程池已达上限 {}，复用已有池", config.maxSessionThreads);
                        // 超过上限时复用一个已有线程池
                        return executors.values().iterator().next();
                    }
                    return Executors.newSingleThreadExecutor();
                }).submit(() -> doCustomer(message));
            }
        }, "zeromq-bus-receiver");
        receiverThread.start();
    }

    public void shutdown() {
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
        executors.values().forEach(ExecutorService::shutdown);
        executors.clear();
    }

    private void doCustomer(String message) {
        Logger.info("消息监听    " + message);
        doInvokeEventListener(message);
    }


    @Override
    public void doPublish(BaseEvent baseEvent) throws IOException, StateTransitinException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        publisher.sendMore(baseEvent.name); // 发送主题
        publisher.send(JsonUtil.obj2String(baseEvent));   // 发送消息
    }


    @Override
    public void doSubscriber(String eventType, BaseEventListener listener) throws MQClientException, InterruptedException {
        if (!eventListenerMap.containsKey(eventType)) {
            subscriber.subscribe(eventType.getBytes());
            eventListenerMap.put(eventType, listener);
        }


    }


}