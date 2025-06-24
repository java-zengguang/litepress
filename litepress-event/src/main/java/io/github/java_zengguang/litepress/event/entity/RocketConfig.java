package io.github.java_zengguang.litepress.event.entity;

public class RocketConfig {
    public String group;   //建议配置为应用名+消息总线名，避免因消费组相同导致的冲突
    public String topic;
    public String namesrvAddr;
    public String accessKey;
    public String secretKey;
    public String consumeThreadMin="1";
    public String consumeThreadMax="5";

    public String retryCount="3";

    public String workMode="P-C";    // P-C -并行集群  S-C-串行集群  P-B  -并行广播   S-B  -串行集群  默认并行集群

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getConsumeThreadMin() {
        return consumeThreadMin;
    }

    public void setConsumeThreadMin(String consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public String getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public void setConsumeThreadMax(String consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    public String getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(String retryCount) {
        this.retryCount = retryCount;
    }
}
