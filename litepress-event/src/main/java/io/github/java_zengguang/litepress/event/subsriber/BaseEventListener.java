package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.event.event.BaseEvent;

public abstract class BaseEventListener implements EventListener {
    ThreadLocal<BaseEvent> threadLocal = new ThreadLocal<>();

    public BaseEvent getBaseEvent() {
        return threadLocal.get();
    }

    //如果需要处理事件、幂等、等操作可以重写 dealEvent方法
    @Override
    public void dealEvent(BaseEvent event) throws Exception {
        threadLocal.set(event);
        callBack(event.message);
    }
}
