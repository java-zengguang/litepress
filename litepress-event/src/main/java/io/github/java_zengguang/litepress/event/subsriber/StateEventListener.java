package io.github.java_zengguang.litepress.event.subsriber;

import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.state.StateModel;
import io.github.java_zengguang.litepress.event.state.po.StatePo;

import java.util.HashMap;
import java.util.Map;

public  class StateEventListener extends BaseEventListener {
    private final Map<String, StateModel> stateModelMap = new HashMap<>();

    public void addStateModel(String modelId,StateModel stateModel){
        stateModelMap.put(modelId,stateModel);
    }
    @Override
    public void dealEvent(BaseEvent event) throws Exception {
        super.dealEvent(event);
        StatePo statePo = event.statePo;
        if (statePo != null) {
            StateModel stateModel = stateModelMap.get(statePo.stateModelId);
            stateModel.doTransitionRule(event);
        }
    }



}
