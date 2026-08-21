package io.github.java_zengguang.litepress.event.state;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.state.action.StateAction;
import io.github.java_zengguang.litepress.event.state.po.EventInstancePo;
import io.github.java_zengguang.litepress.event.state.po.StatePo;
import io.github.java_zengguang.litepress.event.state.rule.StateTransitionRule;
import org.tinylog.Logger;

import java.util.*;

public abstract class BaseStateModel implements StateModel {
    private final String id;
    private Set<String> eventSet = new HashSet<String>();
    private Set<String> stateSet = new HashSet<String>();
    private Map<String, List<StateTransitionRule>> eventTransitionRuleMap = new HashMap<>();


    public BaseStateModel(String id) {
        this.id = id;
    }

    public void addEventTransitionRule(StateTransitionRule stateTransitionRule) {

        String event = stateTransitionRule.event;
        List<String> from = stateTransitionRule.from;
        String to = stateTransitionRule.to;
        if (event != null) {
            eventSet.add(event);
        }
        if (from != null) {
            stateSet.addAll(from);
        }
        if (to != null) {
            stateSet.add(to);
        }

        this.eventTransitionRuleMap.computeIfAbsent(stateTransitionRule.event, (key) -> {
            return new ArrayList<>();
        }).add(stateTransitionRule);
    }




    private boolean checkState(BaseEvent baseEvent, StateTransitionRule stateTransitionRule) {
        if (stateTransitionRule.from == null) {
            return true;
        }
        StatePo currentStatePo = this.getState(baseEvent.instanceId);
        if (!stateTransitionRule.from.contains(currentStatePo.name)) {
            Logger.info("事件%s 实例%s 预期状态%s 实际状态%s 跳过执行".formatted(baseEvent.name,baseEvent.instanceId, stateTransitionRule.from,currentStatePo.name));
            return false;
        }
        if (stateTransitionRule.when != null) {
            return stateTransitionRule.when.deal(baseEvent);
        }
        return true;
    }

    private void changeState(BaseEvent baseEvent, StateTransitionRule stateTransitionRule) {
        if (stateTransitionRule.to != null && checkState(baseEvent, stateTransitionRule)) {
            saveState(baseEvent.instanceId, new StatePo(stateTransitionRule.to, new Date().getTime()));
        }
    }

    //递归任务，状态切换后，直接触发动作
    public void doTransitionRule(BaseEvent baseEvent) throws StateTransitinException {
        if (baseEvent.instanceId != null && eventTransitionRuleMap.containsKey(baseEvent.name)) {
            List<StateTransitionRule> stateTransitionRules = eventTransitionRuleMap.get(baseEvent.name);
            for (StateTransitionRule stateTransitionRule : stateTransitionRules) {
                try {
                    //不满足直接跳过
                    if (!checkState(baseEvent, stateTransitionRule)) {
                        continue;
                    }

                    StateAction action = stateTransitionRule.action;
                    if (action != null) {
                        action.deal(baseEvent);
                    }

                    if (stateTransitionRule.to != null) {
                        changeState(baseEvent, stateTransitionRule);
                    }

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
