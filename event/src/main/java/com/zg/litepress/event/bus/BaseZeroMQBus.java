package com.zg.litepress.event.bus;



import com.zg.litepress.core.util.reflect.JsonUtil;
import com.zg.litepress.event.entity.ZoreMQConfig;
import com.zg.litepress.event.event.BaseEvent;
import com.zg.litepress.event.exception.StateTransitinException;
import com.zg.litepress.event.subsriber.EventListener;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.tinylog.Logger;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseZeroMQBus extends BaseMessageBus implements  MessageBus {

    private ZoreMQConfig zoreMQConfig;

    private ZMQ.Socket publisher;

    private ZMQ.Socket subscriber;

    private ExecutorService customerExecutor ;


    public BaseZeroMQBus(ZoreMQConfig zoreMQConfig) {
        this.zoreMQConfig = zoreMQConfig;
        ZMQ.Context context = ZMQ.context(1);
        publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://*:" + zoreMQConfig.port);
        subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://localhost:" + zoreMQConfig.port);
        customerExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void init() throws MQClientException, InterruptedException {

        Logger.info("初始化");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                // 订阅特定主题
                while (true) {
                    String receivedTopic = subscriber.recvStr(0); // 接收主题
                    String message = subscriber.recvStr(0);       // 接收消息
                    customerExecutor.submit(()->{
                        doCustomer(receivedTopic, message);
                    });
                }
            }
        });
        executor.shutdown();
    }

    private void doCustomer(String topic, String message) {
        Logger.info("消息监听    " + new String(message));
        EventListener eventListener = eventListenerMap.get(topic);
        if (eventListener != null) {
            try {
                BaseEvent baseEvent = JsonUtil.string2Obj(message, BaseEvent.class);
                doInvokeEventListener(eventListener, baseEvent);
            } catch (StateTransitinException e) {
                Logger.error(e);
            }

        }

    }


    @Override
    public void publish(BaseEvent baseEvent) throws IOException, StateTransitinException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        publishBefore(baseEvent);
        publisher.sendMore(baseEvent.eventType); // 发送主题
        publisher.send(JsonUtil.obj2String(baseEvent));   // 发送消息
        publishAfter(baseEvent);

    }


    @Override
    public void subscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException {
        if (!eventListenerMap.containsKey(eventType)) {
            subscriber.subscribe(eventType.getBytes());
            eventListenerMap.put(eventType, listener);
        }


    }



}
