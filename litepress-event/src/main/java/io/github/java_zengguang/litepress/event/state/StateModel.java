package io.github.java_zengguang.litepress.event.state;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.po.StatePo;
import io.github.java_zengguang.litepress.event.state.rule.StateTransitionRule;

import java.util.List;

public interface StateModel {
    void addEventTransitionRule(StateTransitionRule stateTransitionRule);

    void doTransitionRule(BaseEvent baseEvent) throws StateTransitinException;

    List<String> getEvents();

    StatePo getState(String id);

    void saveState(String id, StatePo statePo);
}
