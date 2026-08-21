package io.github.java_zengguang.litepress.event.state.rule;


import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.action.StateAction;
import io.github.java_zengguang.litepress.event.state.action.StateHandler;
import io.github.java_zengguang.litepress.event.state.po.StatePo;
import org.tinylog.Logger;

import java.util.Date;
import java.util.List;

public abstract class BaseStateTransitionRule  {

    public List<String> from;
    public String event;
    public StateHandler when;
    public String to;
    public StateAction action;

//
//    private boolean checkTransitionRule(BaseEvent event) {
//
//        //事件
//        if (this.event != null && !event.name.equals(this.event)) {
//            Logger.error("%s 时间不匹配，当前事件 %s 所需事件 %s".formatted(event.name, event.name, this.when));
//            return false;
//        }
//        //初始状态
//        if (this.from != null && !this.from.isEmpty() && !this.from.contains(event.instance.state.name)) {
//            Logger.error("%s 状态不匹配，当前状态 %s 所需状态 %s".formatted(event.name, event.instance.state.name, this.from));
//            return false;
//        }
//        //条件
//        return this.when == null || this.when.deal(event);
//    }
//
//    @Override
//    public void doTransitionState(BaseEvent event) throws Exception {
//        if (this.checkTransitionRule(event)) {
//            if (action != null) {
//                try {
//                    action.deal(event);
//                    if (this.to != null) {
//                        event.instance.state = new StatePo(this.to,new Date().getTime());
//                    }
//
//                } catch (Exception e) {
//                    throw new StateTransitinException("动作执行失败", e);
//                }
//            }
//
//        }
//
//    }
//
//
//    public String getEvent() {
//        return event;
//    }
//
//    public List<String> getFrom() {
//        return from;
//    }
//
//    public String getTo() {
//        return to;
//    }

}
