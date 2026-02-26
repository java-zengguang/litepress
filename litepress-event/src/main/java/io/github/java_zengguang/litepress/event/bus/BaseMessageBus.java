package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import io.github.java_zengguang.litepress.react.semaphore.SemaphoreManager;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseMessageBus implements MessageBus {

    public Map<String, BaseEventListener> eventListenerMap = new ConcurrentHashMap<>();
    public SemaphoreManager semaphoreManager;

    public void doInvokeEventListener(String body) {
        BaseEvent baseEvent = JsonUtil.string2Obj(body, BaseEvent.class);
        addTrace(baseEvent);  //绑定链路信息
        if (eventListenerMap.containsKey(baseEvent.name)) {
            BaseEventListener eventListener = eventListenerMap.get(baseEvent.name);
            try {
                eventListener.dealEvent(body);
            } catch (Exception e) {
                Logger.error(e, "事件处理异常");
                baseEvent.errorMessage = e.getMessage();
            }
        }

    }

    public void publish(BaseEvent baseEvent) throws Exception {
        doPublish(baseEvent);
      //  doInvokeEventListener(JsonUtil.obj2String(baseEvent));
    }


    public void register(BaseEventListener listener) throws Exception {
        List<String> events = listener.getEvents();
        if (events != null && !events.isEmpty()) {
            for (String event : events) {
                if (!eventListenerMap.containsKey(event)) {
                    eventListenerMap.put(event, listener);
                    doSubscriber(event, listener);
                }
            }
        }

    }


    public void setSemaphoreManager(SemaphoreManager semaphoreManager) {
        this.semaphoreManager = semaphoreManager;
    }


    private void addTrace(BaseEvent baseEvent) {
        if (baseEvent.traceMap != null && !baseEvent.traceMap.isEmpty()) {
            baseEvent.traceMap.forEach(ThreadContext::put);
        }
    }

    public abstract void doPublish(BaseEvent baseEvent) throws Exception;

    public abstract void doSubscriber(String event, BaseEventListener listener) throws Exception;


}
