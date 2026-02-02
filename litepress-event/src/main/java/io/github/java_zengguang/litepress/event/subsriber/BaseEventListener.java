package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.state.rule.StateTransitionRule;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import org.tinylog.Logger;
import org.tinylog.ThreadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseEventListener implements EventListener {
    private ThreadLocal<BaseEvent> threadLocal = new ThreadLocal<>();

    public BaseEventListener() {
    }



    public BaseEvent getBaseEvent() {
        return threadLocal.get();
    }





    //如果需要处理事件、幂等、等操作可以重写 dealEvent方法
    @Override
    public void dealEvent(BaseEvent event) throws Exception {
        addTrace(event);  //绑定链路信息
        threadLocal.set(event);

    }


    public void addTrace(BaseEvent baseEvent) {
        if (baseEvent.traceMap != null && !baseEvent.traceMap.isEmpty()) {
            baseEvent.traceMap.forEach(ThreadContext::put);
        }
    }
}
