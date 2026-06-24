package io.github.java_zengguang.litepress.direction.entity;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

public class ProviderEntity extends MainModel {

    // public String interfaceName;
    public String providerName;

    public String clientVersion;
    public String path;
    public String className;
    public String host;
    public int port;
    public int priority;  //优先级默认为0，且每次占用加1，调用结束-1
    public Long count; //调用次数，用作监控，策略是否有效

    public Long times; //调用时间，用作负载参数

    public Double averageTime = 0.0;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public synchronized void occupy() {
        priority++;

    }

    public synchronized void release(long time) {
        priority--;
        times = times + time;
        count++;
        averageTime = times.doubleValue() / (count + 1);
    }


    public int getPriority() {
        return priority;
    }
}
