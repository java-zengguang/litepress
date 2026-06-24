package io.github.java_zengguang.litepress.label.entity;

public class LabelEntity<T extends LabelMetadata> {
    public String value;  //标签值
    public T labelMetadata;

    public LabelEntity() {
    }

    public LabelEntity(String value, T labelMetadata) {
        this.value = value;
        this.labelMetadata = labelMetadata;
    }
}
