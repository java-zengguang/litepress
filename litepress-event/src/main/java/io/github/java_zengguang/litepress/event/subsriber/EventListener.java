package io.github.java_zengguang.litepress.event.subsriber;


import io.github.java_zengguang.litepress.event.bus.MessageBus;
import io.github.java_zengguang.litepress.event.event.BaseEvent;

import java.util.List;

public interface EventListener {
    //回调message ，事件内容

    List<BaseEvent> dealEvent(String body) throws Exception;

}
