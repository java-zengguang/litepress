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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseRocketMQBus extends BaseMessageBus implements RocketMQManager, MessageBus {
    public Set<String> tagSet = new HashSet<>();

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
        BaseEvent baseEvent = JsonUtil.string2Obj(new String(message.getBody()), BaseEvent.class);
        doInvokeEventListener( baseEvent);
    }

    private Message trans2Message(BaseEvent baseEvent) {
        Logger.info("消息发送    " + baseEvent.eventType + "" + baseEvent.message);
        Message message = new Message(rocketConfig.topic, baseEvent.eventType, baseEvent.eventID, JsonUtil.obj2String(baseEvent).getBytes(StandardCharsets.UTF_8));
        message.putUserProperty("ProtocolType", "EVENT");
        return message;
    }


    @Override
    public void doPublish(BaseEvent baseEvent) throws IOException, StateTransitinException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
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

    }


    @Override
    public void doSubscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException {
        tagSet.add(eventType);
    }


    public String getTags() {
        StringBuffer tags = new StringBuffer("tag");
        tagSet.forEach(x -> {
            tags.append("||" + x);
        });
        return tags.toString();
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
