package com.zg.litepress.event.event.rule;


import com.zg.litepress.event.event.BaseEvent;
import com.zg.litepress.event.exception.StateTransitinException;


public interface EventTransitionRule {
    boolean checkTransitionRule(BaseEvent baseEvent) ;

    void doTransitionState(BaseEvent baseEvent);

    void doAction(BaseEvent baseEvent) throws StateTransitinException;

    public String getNextEvent();

}
