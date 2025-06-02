package com.zg.litepress.router.entity;

import com.zg.litepress.core.bean.entity.MainModel;

public class RouterRegisterConfig extends MainModel {
    public String registerURL;
    public String namespace;
    public String routerType;

    @Override
    public String toString() {
        return "RouterRegisterConfig{" +
                "registerURL='" + registerURL + '\'' +
                ", namespace='" + namespace + '\'' +
                ", routerType='" + routerType + '\'' +
                '}';
    }
}
