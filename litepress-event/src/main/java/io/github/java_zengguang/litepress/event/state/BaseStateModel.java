package io.github.java_zengguang.litepress.event.state;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.po.EventInstancePo;
import io.github.java_zengguang.litepress.event.state.rule.BaseStateTransitionRule;
import io.github.java_zengguang.litepress.event.state.rule.StateTransitionRule;
import org.tinylog.Logger;

import java.util.*;

public abstract class BaseStateModel implements StateModel {
    String stateModelId;
    private Set<String> eventSet = new HashSet<String>();
    private Set<String> stateSet = new HashSet<String>();
    private ThreadLocal<BaseEvent> threadLocal = new ThreadLocal<>();
    private Map<String, List<StateTransitionRule>> eventTransitionRuleMap = new HashMap<>();

    public BaseStateModel(String stateModelId) {
        this.stateModelId = stateModelId;
    }

    public void addEventTransitionRule(BaseStateTransitionRule stateTransitionRule) {

        eventSet.add(stateTransitionRule.getEvent());
        eventSet.add(stateTransitionRule.getFrom());
        eventSet.add(stateTransitionRule.getTo());
        this.eventTransitionRuleMap.computeIfAbsent(stateTransitionRule.getEvent(), (key) -> {
            return new ArrayList<>();
        }).add(stateTransitionRule);
    }


    public BaseEvent getBaseEvent() {
        return threadLocal.get();
    }


    //递归任务，状态切换后，直接触发动作
    public void doTransitionRule(BaseEvent baseEvent) throws StateTransitinException {
        EventInstancePo instancePo = baseEvent.instance;
        if (instancePo != null && eventTransitionRuleMap.containsKey(baseEvent.name)) {
            List<StateTransitionRule> stateTransitionRules = eventTransitionRuleMap.get(baseEvent.name);
            for (StateTransitionRule stateTransitionRule : stateTransitionRules) {
                try {
                     stateTransitionRule.doTransitionState(baseEvent);
                } catch (Exception e) {
                    Logger.error(e);
                    throw new StateTransitinException("执行任务出错！", e);
                }
            }
        }
    }

    public List<String> getEvents() {
        return new ArrayList<>(eventSet);
    }



}
