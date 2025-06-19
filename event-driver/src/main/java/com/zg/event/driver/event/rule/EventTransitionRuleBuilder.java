package com.zg.event.driver.event.rule;


import com.zg.event.driver.event.action.StateAction;
import com.zg.event.driver.event.action.StateHandler;
import com.zg.event.driver.exception.StateTransitinException;

public class EventTransitionRuleBuilder {

    private SimpleEventTransitionRule eventTransitionRule = new SimpleEventTransitionRule();


    public EventTransitionRuleBuilder() {
        eventTransitionRule.form = "0"; //初始化状态，默认
    }

    public static EventTransitionRuleBuilder crate() {
        return new EventTransitionRuleBuilder();
    }

    public EventTransitionRuleBuilder form(String state) {
        eventTransitionRule.form = state;
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

    public EventTransitionRuleBuilder nextEvent(String nextEvent) {
        eventTransitionRule.nextEvent = nextEvent;
        return this;
    }

    public EventTransitionRuleBuilder end() {
        eventTransitionRule.end =true;
        return this;
    }

    public EventTransitionRule build() throws StateTransitinException {
        if(eventTransitionRule.end && eventTransitionRule.nextEvent!=null){
            throw new StateTransitinException("构建错误，终止节点不允许有下一事件节点");
        }
        return this.eventTransitionRule;
    }

}
