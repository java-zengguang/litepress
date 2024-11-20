package com.zg.router.entity;

import com.zg.common.bean.entity.MainModel;

public class RouterEntity extends MainModel {
    public String serviceName;   //服务名称
    public String serviceType;  //  服务类型 MQ  CONFIG  CACHE PROVIDER
    public String path;
    public String clientVersion;
    public String host;
    public Integer port;
    public String url;
}
