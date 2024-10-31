package com.zg.event.driver.bus;






import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.manager.EventStateManager;
import com.zg.event.driver.event.rule.EventTransitionRule;
import com.zg.event.driver.subsriber.EventListener;
import org.apache.rocketmq.client.exception.MQClientException;
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


    public String getTags() {
        StringBuffer tags = new StringBuffer("tag");
        tagSet.forEach(x -> {
            tags.append("||" + x);
        });
        return tags.toString();
    }

    public void addTrace(BaseEvent baseEvent){
        if(baseEvent.traceMap!=null && baseEvent.traceMap.size()>0) {
            baseEvent.traceMap.forEach((key, value) -> {
                ThreadContext.put(key, value);
            });
        }
    }

    public void setEventStateManager(EventStateManager eventStateManager) {
        this.eventStateManager = eventStateManager;
    }
    public void subscriber(String eventType, String eventStage, EventListener listener, EventTransitionRule eventTransitionRule) throws MQClientException, InterruptedException {
        if (eventStateManager != null) {
            eventStateManager.addEventTransitionRule(eventType, eventStage, eventTransitionRule);
        }
        subscriber(eventType, listener);
    }

}
