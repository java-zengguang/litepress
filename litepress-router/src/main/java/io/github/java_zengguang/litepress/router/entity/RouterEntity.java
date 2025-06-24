package io.github.java_zengguang.litepress.router.entity;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

public class RouterEntity extends MainModel {
    public String name;   //服务名称
    public String routerType;  //  服务类型 MQ  CONFIG  CACHE PROVIDER
    public String path;
    public String version;
    public String host;
    public Integer port;
    public Integer state; //状态  0-初始化  1-运行中 2-主动下线  3-服务异常

}
