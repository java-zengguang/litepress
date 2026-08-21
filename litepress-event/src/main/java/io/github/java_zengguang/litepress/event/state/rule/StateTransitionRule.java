package io.github.java_zengguang.litepress.event.state.rule;


import io.github.java_zengguang.litepress.event.state.action.StateAction;
import io.github.java_zengguang.litepress.event.state.action.StateHandler;

import java.util.List;


public class StateTransitionRule {

    // void doTransitionState(BaseEvent baseEvent) throws Exception;


    public List<String> from;
    public String event;
    public StateHandler when;
    public String to;
    public StateAction action;
}
