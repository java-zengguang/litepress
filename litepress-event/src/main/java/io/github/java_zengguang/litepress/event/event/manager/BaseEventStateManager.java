package io.github.java_zengguang.litepress.event.event.manager;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.event.rule.EventTransitionRule;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class BaseEventStateManager implements EventStateManager {

    public Table<String, String, Set<EventTransitionRule>> eventStateMap = HashBasedTable.create();  //事件、阶段、转换规则

    public Set<EventTransitionRule> getEventTransitionRule(String stage, BaseEvent baseEvent) {

        Set<EventTransitionRule> eventTransitionRules = eventStateMap.get(baseEvent.eventType, stage);
        if (eventTransitionRules != null && eventTransitionRules.size() > 0) {
            return eventTransitionRules.stream().filter((x) -> x.checkTransitionRule(baseEvent)).collect(Collectors.toSet());
        }
        return null;
    }

    public void addEventTransitionRule(String event, String stage, EventTransitionRule eventTransitionRule) {
        Set<EventTransitionRule> eventTransitionRules = eventStateMap.get(event, stage);
        if (eventTransitionRules == null) {
            eventTransitionRules = new HashSet<>();
        }
        eventTransitionRules.add(eventTransitionRule);
        eventStateMap.put(event, stage, eventTransitionRules);
    }

}
