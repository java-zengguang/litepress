package io.github.java_zengguang.litepress.label.entity;

public class LabelMetadata {
    public String name;  //标签名
    public String description;  //标签描述
    public int priority;  //标签优先级
    public int weight; //
    public LabelMetadata() {
    }

    public LabelMetadata(String name) {
        this.name = name;
        this.weight=0;
    }
    public LabelMetadata(String name,Integer weight) {
        this.name = name;
        this.weight=weight;
    }
}
