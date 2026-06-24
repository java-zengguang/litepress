package io.github.java_zengguang.litepress.event.state.rule;


import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;


public interface StateTransitionRule {

    void doTransitionState(BaseEvent baseEvent) throws Exception;


}
