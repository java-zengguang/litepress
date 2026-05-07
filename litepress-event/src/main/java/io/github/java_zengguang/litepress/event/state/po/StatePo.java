package io.github.java_zengguang.litepress.event.state.po;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

public class StatePo extends MainModel {
    public String name;
    public Long version;

    public StatePo() {
    }

    public StatePo(String name, Long version) {
        this.name = name;
        this.version = version;
    }
}
