package com.zg.direction.entity;

import com.zg.common.bean.entity.MainModel;

public class ProviderEntity extends MainModel {

    // public String interfaceName;
    public String providerName;
    public String path;
    public String className;
    public String host;
    public int port;
    public int priority;  //优先级默认为0，且每次占用加1，调用结束-1
    public int count; //调用次数，用作监控，策略是否有效

    public long times; //调用时间，用作负载参数



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
        count++;
    }
    public synchronized void release(long time){
        priority--;
        times=times+time;
    }



    public int getPriority() {
        return priority;
    }
}
