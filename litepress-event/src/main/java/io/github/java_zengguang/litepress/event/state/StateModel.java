package io.github.java_zengguang.litepress.event.state;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.rule.BaseStateTransitionRule;
import io.github.java_zengguang.litepress.event.state.rule.StateTransitionRule;

import java.util.List;

public interface StateModel {
    void addEventTransitionRule(BaseStateTransitionRule stateTransitionRule);

    void doTransitionRule(BaseEvent baseEvent) throws StateTransitinException;

    List<String> getEvents();
}
