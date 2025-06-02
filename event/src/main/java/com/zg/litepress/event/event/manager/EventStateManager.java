package com.zg.litepress.event.event.manager;


import com.zg.litepress.event.event.BaseEvent;
import com.zg.litepress.event.event.rule.EventTransitionRule;

import java.util.Set;


public interface EventStateManager {


    void addEventTransitionRule(String event, String stage, EventTransitionRule eventTransitionRule);

    Set<EventTransitionRule> getEventTransitionRule(String stage, BaseEvent baseEvent);

}
