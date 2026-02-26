package io.github.java_zengguang.litepress.event.state.po;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;


public class EventInstancePo extends MainModel {
    public String state;
    public String instanceId;
    public String modelId;

    public EventInstancePo() {
    }

    public EventInstancePo(String state, String instanceId, String modelId) {
        this.state = state;
        this.instanceId = instanceId;
        this.modelId = modelId;
    }
}
