package com.zg.direction.entity;

import com.zg.common.bean.entity.MainModel;

public class ProviderEntity extends MainModel {

    // public String interfaceName;
    public String path;
    public String className;
    public String host;
    public int port;
    public int priority;  //优先级默认为0，且每次占用加1，调用结束-1

    public int count; //调用次数，用作监控，策略是否有效


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

    public synchronized void occupy(){
        priority++;
    }
    public synchronized void release(){
        priority--;
    }

    public synchronized void addCount(){
        count++;
    }

    public int getPriority() {
        return priority;
    }
}
