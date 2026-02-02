package io.github.java_zengguang.litepress.event.state.action;

import io.github.java_zengguang.litepress.event.event.BaseEvent;

public interface StateHandler {
   boolean deal(BaseEvent baseEvent);

}
