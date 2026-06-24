package io.github.java_zengguang.litepress.label.entity;

public class LabelMetadata {
    public final String name;
    public final String description;
    public final int priority;
    public final int weight;

    private LabelMetadata(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.priority = builder.priority;
        this.weight = builder.weight;
    }

    // 从已有对象创建 Builder
    public static Builder builder(LabelMetadata original) {
        return new Builder(original);
    }

    // 静态工厂方法
    public static Builder builder() {
        return new Builder();
    }

    // 从现有对象创建
    public static Builder builderFrom(LabelMetadata original) {
        return builder(original);
    }

    // Getter 方法...
    // 省略重复的 getter 代码

    public static class Builder {
        private String name;
        private String description;
        private int priority;
        private int weight;

        // 默认构造
        public Builder() {
            this.name = "";
            this.description = "";
            this.priority = 0;
            this.weight = 0;
        }

        // 复制构造
        public Builder(LabelMetadata original) {
            if (original != null) {
                this.name = original.name;
                this.description = original.description;
                this.priority = original.priority;
                this.weight = original.weight;
            }
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public LabelMetadata build() {
            return new LabelMetadata(this);
        }
    }
}