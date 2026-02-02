package io.github.java_zengguang.litepress.event.state.rule;


import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.state.action.StateAction;
import io.github.java_zengguang.litepress.event.state.action.StateHandler;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;

public abstract class BaseStateTransitionRule implements StateTransitionRule {

    public String from;
    public String event;
    public StateHandler when;
    public String to;
    public StateAction action;


    @Override
    public boolean checkTransitionRule(BaseEvent baseEvent) {
        //初始状态
        if (!baseEvent.eventState.equals(this.from)) {
            return false;
        }
        //事件
        if (this.event != null && !baseEvent.eventType.equals(this.event)) {
            return false;
        }
        //条件
        if (this.when != null && !this.when.deal(baseEvent)) {
            return false;
        }
        return true;
    }

    @Override
    public void doTransitionState(BaseEvent baseEvent) throws Exception {
        if (this.to != null) {
            baseEvent.eventState = this.to;  //转换状态
        }
    }

    @Override
    public void doAction(BaseEvent baseEvent) throws StateTransitinException {
        if (action != null) {
            try {
                action.deal(baseEvent);
            } catch (Exception e) {
                throw new StateTransitinException("动作执行失败", e);
            }
        }
    }


    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public String getTo() {
        return to;
    }

}
