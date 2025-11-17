package io.github.java_zengguang.litepress.boot;


import io.github.java_zengguang.litepress.core.init.Evn;
import io.github.java_zengguang.litepress.boot.init.Init;

import io.github.java_zengguang.litepress.web.netty.reactor.ReactorWebService;

public abstract class BaseWebAppBoot {

    private Class clazz;
    private Integer port;


    public BaseWebAppBoot(Class clazz, Integer port) {
        this.clazz = clazz;
        this.port = port;
    }


    public void init() {


        Init.doMain(clazz);
        config();
        ReactorWebService tomcatBoot= ReactorWebService.getInstance(port);
        try {
            tomcatBoot.doMain();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public abstract void config();
}
