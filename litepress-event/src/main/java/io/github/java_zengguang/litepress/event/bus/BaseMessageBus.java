package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import io.github.java_zengguang.litepress.react.semaphore.SemaphoreManager;
import org.tinylog.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseMessageBus implements MessageBus {

    public Map<String, BaseEventListener> eventListenerMap = new ConcurrentHashMap<>();
    public SemaphoreManager semaphoreManager;

    public void doInvokeEventListener(String body) {
        BaseEvent baseEvent = JsonUtil.string2Obj(body, BaseEvent.class);
        if (eventListenerMap.containsKey(baseEvent.name)) {
            BaseEventListener eventListener = eventListenerMap.get(baseEvent.name);
            try {
                //透传messageBus，方便实现事件联
                List<BaseEvent> nextEvents = eventListener.dealEvent(body);
                if (nextEvents != null && !nextEvents.isEmpty()) {
                    for (BaseEvent nextEvent : nextEvents) {
                        nextEvent.parentId = baseEvent.id;
                        nextEvent.id = UUID.randomUUID().toString();
                        this.publish(nextEvent);
                    }
                }
            } catch (Exception e) {
                Logger.error(e, "事件处理异常");
                baseEvent.message = "事件处理异常";
            }
        }

    }

    public void publish(BaseEvent baseEvent) throws Exception {
        doPublish(baseEvent);
    }


    public void register(BaseEventListener listener) throws Exception {
        List<String> events = listener.getEvents();
        if (events != null && !events.isEmpty()) {
            for (String event : events) {
                if (!eventListenerMap.containsKey(event)) {
                    eventListenerMap.put(event, listener);
                    doRegister(event, listener);
                }
            }
        }

    }


    public void setSemaphoreManager(SemaphoreManager semaphoreManager) {
        this.semaphoreManager = semaphoreManager;
    }

    public abstract void doPublish(BaseEvent baseEvent) throws Exception;

    public abstract void doRegister(String event, BaseEventListener listener) throws Exception;


}
