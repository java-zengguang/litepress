package com.zg.direction.entity;

import com.zg.bean.entity.MainModel;

public class ProviderEntity extends MainModel {

    // public String interfaceName;
    public String className;
    public String host;
    public int port;


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
}
