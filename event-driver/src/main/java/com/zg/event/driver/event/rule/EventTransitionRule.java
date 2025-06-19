package com.zg.event.driver.event.rule;


import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.exception.StateTransitinException;


public interface EventTransitionRule {
    boolean checkTransitionRule(BaseEvent baseEvent) ;

    void doTransitionState(BaseEvent baseEvent);

    void doAction(BaseEvent baseEvent) throws StateTransitinException;

    public String getNextEvent();

}
