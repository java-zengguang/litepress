package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.event.event.BaseEvent;

import io.github.java_zengguang.litepress.event.subsriber.EventListener;
import io.github.java_zengguang.litepress.react.semaphore.SemaphoreManager;



public interface MessageBus {

    //发布
    void publish(BaseEvent baseEvent) throws Exception;

    void register(String eventType, EventListener listener) throws Exception;

    void setSemaphoreManager(SemaphoreManager semaphoreManager);



}
