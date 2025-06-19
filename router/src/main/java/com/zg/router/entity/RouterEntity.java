package com.zg.router.entity;

import com.zg.common.bean.entity.MainModel;

public class RouterEntity extends MainModel {
    public String name;   //服务名称
    public String routerType;  //  服务类型 MQ  CONFIG  CACHE PROVIDER
    public String path;
    public String version;
    public String host;
    public Integer port;
    public String url;
    public String description;
}
