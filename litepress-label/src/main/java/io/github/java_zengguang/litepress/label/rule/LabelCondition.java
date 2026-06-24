package io.github.java_zengguang.litepress.label.rule;

@FunctionalInterface
public interface LabelCondition {
    LabelCondition NULL = (Object) -> "";
    String evaluate(Object var1) throws Exception;
}
