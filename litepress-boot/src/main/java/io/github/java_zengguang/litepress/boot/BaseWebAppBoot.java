package io.github.java_zengguang.litepress.boot;


import io.github.java_zengguang.litepress.core.init.Evn;
import io.github.java_zengguang.litepress.boot.init.Init;
import io.github.java_zengguang.litepress.web.netty.reactor.ReactorWebService;
import org.tinylog.configuration.Configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public abstract class BaseWebAppBoot {

    private Class clazz;
    private Integer port;


    public BaseWebAppBoot(Class clazz, Integer port) {
        this.clazz = clazz;
        this.port = port;
    }


    public void loadTinyLogConfig() {

        try {
            if (Evn.getInitConfigMap("tinyLogConfig") != null) {
                Properties properties = new Properties();
                properties.load(new StringReader(Evn.getInitConfigMap("tinyLogConfig")));
                properties.forEach((key, value) -> {
                    Configuration.set((String) key, (String) value);
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() {


        Init.doMain(clazz);
        loadTinyLogConfig();
        config();
        ReactorWebService tomcatBoot=new ReactorWebService(port);
        try {
            tomcatBoot.doMain();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public abstract void config();
}
