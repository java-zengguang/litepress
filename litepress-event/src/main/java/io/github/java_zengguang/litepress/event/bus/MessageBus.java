package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;
import io.github.java_zengguang.litepress.react.semaphore.SemaphoreManager;



public interface MessageBus {

    //发布
    void publish(BaseEvent baseEvent) throws Exception;

    void register(BaseEventListener listener) throws Exception;


    void setSemaphoreManager(SemaphoreManager semaphoreManager);


}
