package io.github.java_zengguang.litepress.event.state.rule;


import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.action.StateAction;
import io.github.java_zengguang.litepress.event.state.action.StateHandler;
import org.tinylog.Logger;

import java.util.List;

public abstract class BaseStateTransitionRule<T extends BaseEvent> implements StateTransitionRule<T> {

    public List<String> from;
    public String event;
    public StateHandler when;
    public String to;
    public StateAction action;


    private boolean checkTransitionRule(T t) {

        //事件
        if (this.event != null && !t.name.equals(this.event)) {
            Logger.error("%s 时间不匹配，当前事件 %s 所需事件 %s".formatted(t.name, t.name, this.when));
            return false;
        }
        //初始状态
        if (this.from != null && !this.from.isEmpty() && !this.from.contains(t.instance.state)) {
            Logger.error("%s 状态不匹配，当前状态 %s 所需状态 %s".formatted(t.name, t.instance.state, this.from));
            return false;
        }
        //条件
        return this.when == null || this.when.deal(t);
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

    public List<String> getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
