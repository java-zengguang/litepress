package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.client.RocketMQFactory;


import io.github.java_zengguang.litepress.event.entity.RocketConfig;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.subsriber.EventListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseRocketMQBus extends BaseMessageBus implements RocketMQManager, MessageBus {
    public RocketConfig rocketConfig;
    public DefaultMQPushConsumer consumer;
    public DefaultMQProducer producer;

    public BaseRocketMQBus(RocketConfig rocketConfig) throws InterruptedException, MQClientException {
        this.rocketConfig = rocketConfig;
    }

    public void init() throws MQClientException, InterruptedException {

        RocketMQFactory rocketMQFactory = RocketMQFactory.getInstance();
        this.consumer = rocketMQFactory.getMQConsumer(rocketConfig, getTags());
        this.producer = rocketMQFactory.getMQProducer(rocketConfig);
        if (true) {
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                    try {
                        for (MessageExt message : messages) {
                            doCustomer(message);
                        }
                    } catch (Exception e) {
                        Logger.error(e);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

                }
            });

            consumer.start();
            producer.start();
        }

    }


    private void doCustomer(Message message) {
        Logger.info("消息监听    " + new String(message.getBody()));
        EventListener eventListener = eventListenerMap.get(message.getTags());
        if (eventListener != null) {
            try {
                if ("EVENT".equals(message.getProperty("ProtocolType"))) {   //老版本消息不带这个属性，用于区分协议是直接message还是有event封装
                    BaseEvent baseEvent = JsonUtil.string2Obj(new String(message.getBody()), BaseEvent.class);
                    doInvokeEventListener(eventListener, baseEvent);
                } else {
                    try {
                        eventListener.dealEvent(new BaseEvent("oldEvent", new String(message.getBody())));
                    } catch (Exception e) {
                        Logger.error("事件处理异常" + e);
                    }
                }
            } catch (StateTransitinException e) {
                Logger.error(e);
            }

        }

    }

    private Message trans2Message(BaseEvent baseEvent) {
        Logger.info("消息发送    " + baseEvent.eventType + "" + baseEvent.message);
        Message message = new Message(rocketConfig.topic, baseEvent.eventType, baseEvent.eventID, JsonUtil.obj2String(baseEvent).getBytes(StandardCharsets.UTF_8));
        message.putUserProperty("ProtocolType", "EVENT");
        return message;
    }


    @Override
    public void publish(BaseEvent baseEvent) throws IOException, StateTransitinException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        publishBefore(baseEvent);
        if (rocketConfig.workMode.contains("S")) {   //串行只发到一个broker里
            producer.send(trans2Message(baseEvent), new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                    return list.get(0);
                }
            }, null);
        } else {  //并行
            producer.send(trans2Message(baseEvent));
        }
        publishAfter(baseEvent);
    }


    @Override
    public void subscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException {
        if (!eventListenerMap.containsKey(eventType)) {
            tagSet.add(eventType);
            eventListenerMap.put(eventType, listener);
        }


    }



    @Override
    public void suspendCustomer() {
        consumer.suspend();
    }

    @Override
    public void resumeCustomer() {
        consumer.resume();
    }


}
