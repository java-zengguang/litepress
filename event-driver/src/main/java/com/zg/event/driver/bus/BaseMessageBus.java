package com.zg.event.driver.bus;


import com.zg.database.react.semaphore.SemaphoreManager;
import com.zg.event.driver.en.EventStage;
import com.zg.event.driver.en.ProcessState;
import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.manager.EventStateManager;
import com.zg.event.driver.event.rule.EventTransitionRule;
import com.zg.event.driver.exception.StateTransitinException;
import com.zg.event.driver.subsriber.EventListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseMessageBus implements MessageBus {

    public Set<String> tagSet = new HashSet<>();
    public BlockingQueue<BaseEvent> eventQueue = new LinkedBlockingQueue<>(); //阻塞队列，没有消息时阻塞，事件发布
    public Map<String, EventListener> eventListenerMap = new ConcurrentHashMap<>();
    public EventStateManager eventStateManager;
    public SemaphoreManager semaphoreManager;

    public void doInvokeEventListener(EventListener eventListener, BaseEvent baseEvent) throws StateTransitinException {
        processBefore(baseEvent);
        try {
            eventListener.dealEvent(baseEvent);
        } catch (Exception e) {
            Logger.error("事件处理异常", e);
            baseEvent.errorMessage = e.getMessage();
            baseEvent.processState = ProcessState.FAILURE.name();  //遇到异常将流程修改为异常终止
        }
        processAfter(baseEvent);
    }


    public void publishBefore(BaseEvent baseEvent) throws StateTransitinException {
        addTrace(baseEvent);  //绑定链路信息 去掉流程状态的校验
        if (semaphoreManager != null) {
            semaphoreManager.setSemaphore(baseEvent.eventID+"_idempotent", 1);  //设置信号量，用于幂等控制
        }
    }

    public void publishAfter(BaseEvent baseEvent) throws StateTransitinException {
        doEventTransitionRules(EventStage.PUBLISH.name(), baseEvent);  //发布后执行状态机
    }

    //执行事件流转规则
    private void doEventTransitionRules(String stage, BaseEvent baseEvent) throws StateTransitinException {
        if (eventStateManager != null) {
            baseEvent.eventStage = stage;
            Set<EventTransitionRule> eventTransitionRuleSet = eventStateManager.getEventTransitionRule(stage, baseEvent);
            if (eventTransitionRuleSet != null && eventTransitionRuleSet.size() > 0) {
                for (EventTransitionRule eventTransitionRule : eventTransitionRuleSet) {
                    eventTransitionRule.doTransitionState(baseEvent);
                    eventTransitionRule.doAction(baseEvent);
                    try {
                        String nextEvent = eventTransitionRule.getNextEvent();
                        if (nextEvent != null) {
                            baseEvent.nextBaseEvent(nextEvent);
                            publish(baseEvent);
                        }
                    } catch (Exception e) {
                        new StateTransitinException(baseEvent.eventID + "事件流中断，事件发送异常", e);
                    }
                }
            }
        }

    }


    public void processAfter(BaseEvent baseEvent) throws StateTransitinException {
        doEventTransitionRules(baseEvent.eventStage, baseEvent);
    }

    public void processBefore(BaseEvent baseEvent) throws StateTransitinException {
        addTrace(baseEvent);  //绑定链路信息
        //一次发布只能一次消费
        if (semaphoreManager != null) {
            if (semaphoreManager.checkSemaphore(baseEvent.eventID+"idempotent")) {  //信号量存在，且<1 说明已经进行过消费，触发幂等限制
                new StateTransitinException("幂等控制，消息重复消费！eventID " + baseEvent.eventID);
            }
            semaphoreManager.decrementSemaphore(baseEvent.eventID+"idempotent");
        }
        //第一个事件监听把初始化状态改成运行中
        if (ProcessState.INIT.name().equals(baseEvent.processState)) {
            baseEvent.processState = ProcessState.PROGRESS.name();
        }
        doEventTransitionRules(EventStage.PROGRESS.name(), baseEvent);
    }

    public String getTags() {
        StringBuffer tags = new StringBuffer("tag");
        tagSet.forEach(x -> {
            tags.append("||" + x);
        });
        return tags.toString();
    }

    public void addTrace(BaseEvent baseEvent) {
        if (baseEvent.traceMap != null && baseEvent.traceMap.size() > 0) {
            baseEvent.traceMap.forEach((key, value) -> {
                ThreadContext.put(key, value);
            });
        }
    }

    public void setEventStateManager(EventStateManager eventStateManager) {
        this.eventStateManager = eventStateManager;
    }

    public void setSemaphoreManager(SemaphoreManager semaphoreManager) {
        this.semaphoreManager = semaphoreManager;
    }

    public void subscriber(String eventType, String eventStage, EventListener listener, EventTransitionRule eventTransitionRule) throws MQClientException, InterruptedException {
        if (eventStateManager != null) {
            eventStateManager.addEventTransitionRule(eventType, eventStage, eventTransitionRule);
        }
        subscriber(eventType, listener);
    }

}
