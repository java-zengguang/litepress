package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.event.rule.EventTransitionRule;

public interface EventListener {
    //回调message ，事件内容

    void dealEvent(BaseEvent event) throws Exception;


}
