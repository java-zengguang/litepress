package io.github.java_zengguang.litepress.event.state;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.rule.StateTransitionRule;

public interface StateModel {
    void addEventTransitionRule(StateTransitionRule stateTransitionRule);

    void doTransitionRule(BaseEvent baseEvent) throws StateTransitinException;
}
