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


    private ZMQ.Socket publisher;

    private ZMQ.Socket subscriber;

    private final ConcurrentHashMap<String, ExecutorService> executors = new ConcurrentHashMap<>();



    public BaseZeroMQBus() {
        ZMQ.Context context = ZMQ.context(1);
        // 使用PAIR模式
        publisher = context.socket(ZMQ.PAIR);
        subscriber = context.socket(ZMQ.PAIR);
        publisher.bind("inproc://zeromq-bus");
        subscriber.connect("inproc://zeromq-bus");
    }


    // 按用户/会话ID分组，相同ID的顺序处理

    public void init() {
        new Thread(() -> {
            while (true) {
                String topic = subscriber.recvStr(0);
                String message = subscriber.recvStr(0);
                BaseEvent baseEvent=JsonUtil.string2Obj(message,BaseEvent.class);
                String sessionId = baseEvent.accessId; // 提取会话ID

                executors.computeIfAbsent(sessionId, k ->
                        Executors.newSingleThreadExecutor()
                ).submit(() -> doCustomer(message));
            }
        }).start();
    }

    private void doCustomer( String message) {
        Logger.info("消息监听    " + new String(message));
    //    BaseEvent baseEvent = JsonUtil.string2Obj(message, BaseEvent.class);
        doInvokeEventListener( message);
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
