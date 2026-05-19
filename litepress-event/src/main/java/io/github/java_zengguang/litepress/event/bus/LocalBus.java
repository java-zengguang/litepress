package io.github.java_zengguang.litepress.event.bus;


import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.subsriber.BaseEventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocalBus extends BaseMessageBus implements MessageBus {

    private final ExecutorService executor;

    public LocalBus() {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    @Override
    public void doPublish(BaseEvent baseEvent) {
        executor.submit(() -> doInvokeEventListener(JsonUtil.obj2String(baseEvent)));
    }

    @Override
    public void doRegister(String eventType, BaseEventListener listener) {
        eventListenerMap.putIfAbsent(eventType, listener);
    }

}