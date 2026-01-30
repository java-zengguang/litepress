package io.github.java_zengguang.litepress.event.event.rule;


import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;


public interface EventTransitionRule {
    boolean checkTransitionRule(BaseEvent baseEvent);

    void doTransitionState(BaseEvent baseEvent) throws Exception;

    void doAction(BaseEvent baseEvent) throws StateTransitinException;

    String getEvent();

    String getFrom();


}
