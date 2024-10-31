package com.zg.event.driver.bus;


import com.alibaba.fastjson.JSON;
import com.zg.event.driver.client.RocketMQFactory;
import com.zg.event.driver.en.EventStage;
import com.zg.event.driver.en.ProcessState;

import com.zg.event.driver.entity.RocketConfig;
import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.rule.EventTransitionRule;
import com.zg.event.driver.exception.StateTransitinException;
import com.zg.event.driver.subsriber.EventListener;
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
import java.util.Set;

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
                    BaseEvent baseEvent = JSON.parseObject(message.getBody(), BaseEvent.class);
                    //第一个事件监听把初始化状态改成运行中
                    if (ProcessState.INIT.name().equals(baseEvent.processState)) {
                        baseEvent.processState = ProcessState.PROGRESS.name();
                    }
                    //如果流程为运行中，执行监听逻辑
                    if (ProcessState.PROGRESS.name().equals(baseEvent.processState)) {  //只有流程状态为2的时候，才是
                        addTrace(baseEvent);  //绑定链路信息
                        doEventTransitionRules(EventStage.PROGRESS.name(), baseEvent);
                        try {
                            eventListener.dealEvent(baseEvent);
                            doEventTransitionRules(EventStage.SUCCESSFUL.name(), baseEvent);
                        } catch (Exception e) {
                            Logger.error("事件处理异常" + e);
                            baseEvent.errorMessage = e.getMessage();
                            doEventTransitionRules(EventStage.FAILURE.name(), baseEvent);
                            baseEvent.processState = ProcessState.FAILURE.name();  //遇到异常将流程修改为异常终止
                        }
                    }

                } else {
                    try {
                        eventListener.dealEvent(new BaseEvent("oldEvent",new String(message.getBody())));
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
        Message message = new Message(rocketConfig.topic, baseEvent.eventType, baseEvent.eventID, JSON.toJSONString(baseEvent).getBytes(StandardCharsets.UTF_8));
        message.putUserProperty("ProtocolType", "EVENT");
        return message;
    }


    @Override
    public void publish(BaseEvent baseEvent) throws IOException, StateTransitinException, MQBrokerException, RemotingException, InterruptedException, MQClientException {
        addTrace(baseEvent);  //绑定链路信息

        // 将初始状态的，流程状态转化为进行中
        if ("-1".equals(baseEvent.processState)) {
            throw new StateTransitinException("流程已经因异常终止");
        }
        if ("1".equals(baseEvent.processState)) {
            throw new StateTransitinException("流程已经完成");
        }
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


        doEventTransitionRules(EventStage.INIT.name(), baseEvent);  //发布后执行状态机

    }


    @Override
    public void subscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException {
        if (!eventListenerMap.containsKey(eventType)) {
            tagSet.add(eventType);
            eventListenerMap.put(eventType, listener);
        }


    }

    //执行事件流转规则
    public void doEventTransitionRules(String stage, BaseEvent baseEvent) throws StateTransitinException {
        if (eventStateManager != null) {
            baseEvent.eventStage = stage;
            Set<EventTransitionRule> eventTransitionRuleSet = eventStateManager.getEventTransitionRule(stage, baseEvent);
           if(eventTransitionRuleSet!=null && eventTransitionRuleSet.size()>0) {
               for (EventTransitionRule eventTransitionRule : eventTransitionRuleSet) {
                   eventTransitionRule.doTransitionState(baseEvent);
                   eventTransitionRule.doAction(baseEvent);
                   try {
                       String nextEvent = eventTransitionRule.getNextEvent();
                       if (nextEvent != null) {
                           baseEvent.eventType = nextEvent;
                           publish(baseEvent);
                       }
                   }catch (Exception e){
                       new StateTransitinException(baseEvent.eventID+"事件流中断，事件发送异常",e);
                   }
               }
           }
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
