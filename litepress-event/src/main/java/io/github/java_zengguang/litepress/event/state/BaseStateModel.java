package io.github.java_zengguang.litepress.event.state;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.rule.StateTransitionRule;
import org.tinylog.Logger;

import java.util.*;

public abstract class BaseStateModel implements StateModel {
    String stateModelId;
    private Set<String> eventSet=new HashSet<String>();
    private Set<String> stateSet=new HashSet<String>();
    private ThreadLocal<BaseEvent> threadLocal = new ThreadLocal<>();
    private Map<String, List<StateTransitionRule>> eventTransitionRuleMap = new HashMap<>();

    public BaseStateModel(String stateModelId) {
        this.stateModelId=stateModelId;
    }

    public void addEventTransitionRule(StateTransitionRule stateTransitionRule) {
        eventSet.add(stateTransitionRule.getEvent());
        eventSet.add(stateTransitionRule.getFrom());
        eventSet.add(stateTransitionRule.getTo());
        this.eventTransitionRuleMap.computeIfAbsent(stateTransitionRule.getFrom(), (key) -> {
            return new ArrayList<>();
        }).add(stateTransitionRule);
    }



    public BaseEvent getBaseEvent() {
        return threadLocal.get();
    }


    //递归任务，状态切换后，直接触发动作
    public void doTransitionRule(BaseEvent baseEvent) throws StateTransitinException {
        if (eventTransitionRuleMap.containsKey(baseEvent.eventState)) {
            List<StateTransitionRule> stateTransitionRules = eventTransitionRuleMap.get(baseEvent.eventState);
            for (StateTransitionRule stateTransitionRule : stateTransitionRules) {
                try {
                    if (stateTransitionRule.checkTransitionRule(baseEvent)) {
                        stateTransitionRule.doAction(baseEvent);
                        stateTransitionRule.doTransitionState(baseEvent);
                        saveEventState(baseEvent);
                    }
                } catch (Exception e) {
                    Logger.error(e);
                    throw new StateTransitinException("执行任务出错！", e);
                }
            }
        }
    }
    public abstract void saveEventState(BaseEvent baseEvent);

}
