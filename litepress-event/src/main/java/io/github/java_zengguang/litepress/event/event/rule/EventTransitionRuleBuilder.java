package io.github.java_zengguang.litepress.event.event.rule;


import io.github.java_zengguang.litepress.event.event.action.StateAction;
import io.github.java_zengguang.litepress.event.event.action.StateHandler;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;

public class EventTransitionRuleBuilder {

    private SimpleEventTransitionRule eventTransitionRule = new SimpleEventTransitionRule();


    public EventTransitionRuleBuilder() {
        eventTransitionRule.from = "0"; //初始化状态，默认
    }

    public static EventTransitionRuleBuilder crate() {
        return new EventTransitionRuleBuilder();
    }

    public EventTransitionRuleBuilder form(String state) {
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

    public EventTransitionRule build() throws StateTransitinException {
        return this.eventTransitionRule;
    }

}
