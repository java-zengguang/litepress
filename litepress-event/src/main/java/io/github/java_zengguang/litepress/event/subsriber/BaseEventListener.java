package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import org.tinylog.ThreadContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class BaseEventListener<T extends BaseEvent> implements EventListener<T> {
    public Class<T> tClass;
    private Set<String> events;
    private ThreadLocal<BaseEvent> threadLocal = new ThreadLocal<>();

    public BaseEventListener(Class<T> tClass) {
        this.tClass = tClass;
        this.events = new HashSet<>();
    }

    public BaseEventListener(Class<T> tClass, Set<String> events) {
        this.tClass = tClass;
        this.events = events;
    }




    public List<String> getEvents() {
        return new ArrayList<>(events);
    }

    public void addEvents(List<String> events) {
        this.events.addAll(events);
    }

    public void dealEvent(String body) throws Exception {
        T event = JsonUtil.string2Obj(body, tClass);
        init(event);
        beforeDealEvent(event);
        dealEvent(event);
        afterDealEvent(event);
    }

    public abstract void init(T t) throws Exception;

    //如果需要处理事件、幂等、等操作可以重写 dealEvent方法
    public abstract void dealEvent(T event) throws Exception;

    public abstract void beforeDealEvent(T event) throws Exception;


    public abstract void afterDealEvent(T event) throws Exception;


}
