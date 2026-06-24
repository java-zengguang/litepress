package io.github.java_zengguang.litepress.event.state.rule;


import io.github.java_zengguang.litepress.event.state.action.StateAction;
import io.github.java_zengguang.litepress.event.state.action.StateHandler;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;

import java.util.List;

public class EventTransitionRuleBuilder {

    private SimpleStateTransitionRule eventTransitionRule = new SimpleStateTransitionRule();


    public EventTransitionRuleBuilder() {
    }

    public static EventTransitionRuleBuilder crate() {
        return new EventTransitionRuleBuilder();
    }

    public EventTransitionRuleBuilder form(List<String> state) {
        eventTransitionRule.from = state;
        return this;
    }

    public EventTransitionRuleBuilder event(String state) {
        eventTransitionRule.event = state;
        return this;
    }

    public EventTransitionRuleBuilder to(String state) {
        eventTransitionRule.to = state;
        return this;
    }

    public EventTransitionRuleBuilder when(StateHandler state) {
        eventTransitionRule.when = state;
        return this;
    }

    public EventTransitionRuleBuilder action(StateAction state) {
        eventTransitionRule.action = state;
        return this;
    }

    public SimpleStateTransitionRule build() throws StateTransitinException {
        return this.eventTransitionRule;
    }

}
