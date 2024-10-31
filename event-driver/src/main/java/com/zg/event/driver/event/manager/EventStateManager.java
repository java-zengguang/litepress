package com.zg.event.driver.event.manager;


import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.rule.EventTransitionRule;

import java.util.Set;


public interface EventStateManager {


    void addEventTransitionRule(String event, String stage, EventTransitionRule eventTransitionRule);

    Set<EventTransitionRule> getEventTransitionRule(String stage, BaseEvent baseEvent);




}
