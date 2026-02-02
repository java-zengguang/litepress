package io.github.java_zengguang.litepress.event.state.action;

import io.github.java_zengguang.litepress.event.event.BaseEvent;

public interface StateAction {
   void deal(BaseEvent baseEvent) throws Exception;
}
