package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.event.BaseEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class BaseEventListener implements EventListener {
    private Set<String> events;

    public BaseEventListener() {
        this.events = new HashSet<>();
    }

    public BaseEventListener(Set<String> events) {
        this.events = events;
    }



    public List<String> getEvents() {
        return new ArrayList<>(events);
    }

    public void addEvents(List<String> events) {
        this.events.addAll(events);
    }

    public List<BaseEvent> dealEvent(String body) throws Exception {
        BaseEvent event = JsonUtil.string2Obj(body,BaseEvent.class);
        beforeDealEvent(event);
        dealEvent(event);
        afterDealEvent(event);
        if (event.nextEvents != null && !event.nextEvents.isEmpty()) {
            return event.nextEvents;
        }
        return null;
    }


    public abstract void beforeDealEvent(BaseEvent event) throws Exception;

    //如果需要处理事件、幂等、等操作可以重写 dealEvent方法
    public abstract void dealEvent(BaseEvent event) throws Exception;

    public abstract void afterDealEvent(BaseEvent event) throws Exception;

}
