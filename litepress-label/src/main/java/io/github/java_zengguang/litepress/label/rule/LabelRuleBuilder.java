package io.github.java_zengguang.litepress.label.rule;

import io.github.java_zengguang.litepress.label.entity.LabelMetadata;
import org.jeasy.rules.api.Rule;

import java.util.ArrayList;
import java.util.List;

public class LabelRuleBuilder {
    private String name = "rule";
    private String description = "description";
    private int priority = 2147483646;
    private LabelCondition condition;
    private final List<LabelAction> actions = new ArrayList<>();
    private List<LabelMetadata> labelItems = new ArrayList<>();

    public LabelRuleBuilder() {
        this.condition = LabelCondition.NULL;
    }

    public LabelRuleBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LabelRuleBuilder description(String description) {
        this.description = description;
        return this;
    }

    public LabelRuleBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public LabelRuleBuilder items(List<LabelMetadata> labelItems) {
        this.labelItems = labelItems;
        return this;
    }

    public LabelRuleBuilder when(LabelCondition condition) {
        this.condition = condition;
        return this;
    }

    public LabelRuleBuilder then(LabelAction action) {
        this.actions.add(action);
        return this;
    }

    public Rule build() {
        return new LabelRule(this.name, this.description, this.priority, this.condition, this.actions, this.labelItems);
    }
}
