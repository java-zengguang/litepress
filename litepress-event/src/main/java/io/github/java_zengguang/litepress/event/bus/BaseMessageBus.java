package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.event.en.ProcessState;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.subsriber.EventListener;
import io.github.java_zengguang.litepress.react.semaphore.SemaphoreManager;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseMessageBus implements MessageBus {

    public BlockingQueue<BaseEvent> eventQueue = new LinkedBlockingQueue<>(); //阻塞队列，没有消息时阻塞，事件发布
    public Map<String, EventListener> eventListenerMap = new ConcurrentHashMap<>();
    public SemaphoreManager semaphoreManager;

    public void doInvokeEventListener(BaseEvent baseEvent) {
        addTrace(baseEvent);  //绑定链路信息
        if (eventListenerMap.containsKey(baseEvent.eventType)) {
            EventListener eventListener = eventListenerMap.get(baseEvent.eventType);
            //一次发布只能一次消费
            if (semaphoreManager != null) {
                if (semaphoreManager.checkSemaphore(baseEvent.eventID + "idempotent")) {  //信号量存在，且<1 说明已经进行过消费，触发幂等限制
                    new StateTransitinException("幂等控制，消息重复消费！eventID " + baseEvent.eventID);
                }
                semaphoreManager.decrementSemaphore(baseEvent.eventID + "idempotent");
            }
            //第一个事件监听把初始化状态改成运行中
            if (ProcessState.INIT.name().equals(baseEvent.processState)) {
                baseEvent.processState = ProcessState.PROGRESS.name();
            }
            try {
                eventListener.dealEvent(baseEvent);
            } catch (Exception e) {
                Logger.error(e,"事件处理异常");
                baseEvent.errorMessage = e.getMessage();
                baseEvent.processState = ProcessState.FAILURE.name();  //遇到异常将流程修改为异常终止
            }
        }

    }

    public void publish(BaseEvent baseEvent) throws Exception {
        addTrace(baseEvent);  //绑定链路信息 去掉流程状态的校验
        if (semaphoreManager != null) {
            semaphoreManager.setSemaphore(baseEvent.eventID + "_idempotent", 1);  //设置信号量，用于幂等控制
        }
        doPublish(baseEvent);
        doInvokeEventListener(baseEvent);
    }


    public void register(String event, EventListener listener) throws Exception {
        doSubscriber(event, listener);
        if (!eventListenerMap.containsKey(event)) {
            eventListenerMap.put(event, listener);
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

    public abstract void doSubscriber(String event, EventListener listener) throws Exception;


}
