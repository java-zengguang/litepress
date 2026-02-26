package io.github.java_zengguang.litepress.event.state.rule;


import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.action.StateAction;
import io.github.java_zengguang.litepress.event.state.action.StateHandler;

public abstract class BaseStateTransitionRule<T extends BaseEvent> implements StateTransitionRule<T> {

    public String from;
    public String event;
    public StateHandler when;
    public String to;
    public StateAction action;


    private boolean checkTransitionRule(T t) {
        //初始状态
        if (!t.instance.state.equals(this.from)) {
            return false;
        }
        //事件
        if (this.event != null && !t.name.equals(this.event)) {
            return false;
        }
        //条件
        if (this.when != null && !this.when.deal(t)) {
            return false;
        }
        return true;
    }

    @Override
    public void doTransitionState(T t) throws Exception {
        if (this.checkTransitionRule(t)) {
            if (action != null) {
                try {
                    action.deal(t);
                    if (this.to != null) {

                        t.instance.state = this.to;
                    }

                } catch (Exception e) {
                    throw new StateTransitinException("动作执行失败", e);
                }
            }

        }

    }


    public String getEvent() {
        return event;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
