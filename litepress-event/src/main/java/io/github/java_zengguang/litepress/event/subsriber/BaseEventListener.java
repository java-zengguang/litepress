package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.event.rule.EventTransitionRule;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseEventListener implements EventListener {
    private ThreadLocal<BaseEvent> threadLocal = new ThreadLocal<>();
    private Map<String, List<EventTransitionRule>> eventTransitionRuleMap = new HashMap<>();

    public BaseEventListener() {
    }

    public void addEventTransitionRule(EventTransitionRule eventTransitionRule) {
        this.eventTransitionRuleMap.computeIfAbsent(eventTransitionRule.getFrom(), (key) -> {
            return new ArrayList<>();
        }).add(eventTransitionRule);
    }

    public BaseEvent getBaseEvent() {
        return threadLocal.get();
    }


    //递归任务，状态切换后，直接触发动作
    private void doTransitionRule(BaseEvent baseEvent) throws StateTransitinException {
        if (eventTransitionRuleMap.containsKey(baseEvent.eventState)) {
            List<EventTransitionRule> eventTransitionRules = eventTransitionRuleMap.get(baseEvent.eventState);
            for (EventTransitionRule eventTransitionRule : eventTransitionRules) {
                try {
                    if (eventTransitionRule.checkTransitionRule(baseEvent)) {
                        eventTransitionRule.doAction(baseEvent);
                        eventTransitionRule.doTransitionState(baseEvent);
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

    //如果需要处理事件、幂等、等操作可以重写 dealEvent方法
    @Override
    public void dealEvent(BaseEvent event) throws Exception {
        addTrace(event);  //绑定链路信息
        threadLocal.set(event);
        doTransitionRule(event);

    }


    public void addTrace(BaseEvent baseEvent) {
        if (baseEvent.traceMap != null && !baseEvent.traceMap.isEmpty()) {
            baseEvent.traceMap.forEach(ThreadContext::put);
        }
    }
}
