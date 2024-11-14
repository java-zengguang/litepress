package com.zg.event.driver.bus;


import com.zg.common.util.reflect.JsonUtil;
import com.zg.event.driver.en.EventStage;
import com.zg.event.driver.en.ProcessState;

import com.zg.event.driver.entity.ZoreMQConfig;
import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.rule.EventTransitionRule;
import com.zg.event.driver.exception.StateTransitinException;
import com.zg.event.driver.subsriber.EventListener;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.tinylog.Logger;
import org.zeromq.ZMQ;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public  class BaseZeroMQBus extends BaseMessageBus implements RocketMQManager, MessageBus {

    private ZoreMQConfig zoreMQConfig;

    private ZMQ.Socket publisher;

    private ZMQ.Socket subscriber;

    public BaseZeroMQBus(ZoreMQConfig zoreMQConfig) {
        this.zoreMQConfig = zoreMQConfig;
        ZMQ.Context context = ZMQ.context(1);
        publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://*:"+zoreMQConfig.port);
        subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://localhost:"+zoreMQConfig.port);
    }

    public void init() throws MQClientException, InterruptedException {

        Logger.info("初始化");
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                // 订阅特定主题
                while (true) {
                    String receivedTopic = subscriber.recvStr(0); // 接收主题
                    String message = subscriber.recvStr(0);       // 接收消息
                    doCustomer(receivedTopic,message);
                }
            }
        });
        executor.shutdown();
    }

    private void doCustomer(String topic,String message) {
        Logger.info("消息监听    " + new String(message));
        EventListener eventListener = eventListenerMap.get(topic);
        if (eventListener != null) {
            try {
                BaseEvent baseEvent = JsonUtil.string2Obj(message, BaseEvent.class);
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
            } catch (StateTransitinException e) {
                Logger.error(e);
            }

        }

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
        publisher.sendMore(baseEvent.eventType); // 发送主题
        publisher.send(JsonUtil.obj2String(baseEvent));   // 发送消息
        doEventTransitionRules(EventStage.INIT.name(), baseEvent);  //发布后执行状态机

    }


    @Override
    public void subscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException {
        if (!eventListenerMap.containsKey(eventType)) {
            subscriber.subscribe(eventType.getBytes());
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

    }

    @Override
    public void resumeCustomer() {

    }


}
