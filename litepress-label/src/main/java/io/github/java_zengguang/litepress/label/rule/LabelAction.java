package io.github.java_zengguang.litepress.label.rule;


import io.github.java_zengguang.litepress.label.entity.LabelEntity;

import java.util.Set;

@FunctionalInterface
public interface LabelAction {
    void execute(Set<LabelEntity> labels) throws Exception;
}

