package io.github.java_zengguang.litepress.event.subsriber.components;

import io.github.java_zengguang.litepress.event.event.BaseEvent;

public interface EventComponent {

    BaseEvent dealEvent(BaseEvent baseEvent) throws Exception;

}
