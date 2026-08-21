package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.state.StateModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseStateEventListener extends BaseEventListener {
    private final StateModel stateModel;


    public BaseStateEventListener(StateModel stateModel) {
        this.stateModel = stateModel;
        List<String> events = stateModel.getEvents();
        this.addEvents(events);
    }


    //如果需要处理事件、幂等、等操作可以重写 dealEvent方法
    public void dealEvent(BaseEvent event) throws Exception {
        if (event.instanceId != null) {
            stateModel.doTransitionRule(event);
        }

    }


}
