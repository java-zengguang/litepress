package io.github.java_zengguang.litepress.event.event.manager;


import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.event.rule.EventTransitionRule;

import java.util.Set;


public interface EventStateManager {


    void addEventTransitionRule(String event, String stage, EventTransitionRule eventTransitionRule);

    Set<EventTransitionRule> getEventTransitionRule(String stage, BaseEvent baseEvent);

}
