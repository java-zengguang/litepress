package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.event.event.BaseEvent;

public interface EventListener<T extends BaseEvent> {
    //回调message ，事件内容

    void dealEvent(String body) throws Exception;

}
