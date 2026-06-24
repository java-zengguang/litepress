package io.github.java_zengguang.litepress.label.sql;

import org.jeasy.rules.api.Rule;

public class LabelSQLRuleBuilder {
    private String name = "rule";
    private String description = "description";
    private int priority = 2147483646;
    private String dataSource;
    private String sql;

    public LabelSQLRuleBuilder() {
    }

    public LabelSQLRuleBuilder name(String name) {
        this.name = name;
        return this;
    }

    public LabelSQLRuleBuilder description(String description) {
        this.description = description;
        return this;
    }

    public LabelSQLRuleBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public LabelSQLRuleBuilder dataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public LabelSQLRuleBuilder sql(String sql) {
        this.sql = sql;
        return this;
    }

    public Rule build() {
        return new LabelSQLRule(this.name, this.description, this.priority, this.dataSource, this.sql);
    }
}
