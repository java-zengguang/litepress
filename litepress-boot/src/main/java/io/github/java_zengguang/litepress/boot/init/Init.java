package io.github.java_zengguang.litepress.boot.init;


import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.core.init.PropertyConfig;
import org.tinylog.configuration.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Init {

    private static String environment;

    public static void init() {
        try {
            //加载当前环境
            environment = System.getenv().getOrDefault("TargetEvn", "dev");
            initConfig();
            System.out.println("当前环境 %s ".formatted(environment));
        } catch (Exception e) {
            System.out.println("配置文件加载失败！");
        }
    }

    public static String getEnvironment() {
        return environment;
    }


    private static void initConfig() throws IOException {
        //初始化日志
        List<String> logConfigNames = new ArrayList<>();
        logConfigNames.add("config.properties");
        if (environment != null) {
            logConfigNames.add("%s_config.properties".formatted(environment));
        }
        //加载config配置
        PropertyConfig propertyConfig = PropertyConfig.getInstance();
        for (String logConfigName : logConfigNames) {
            String doc = readFile2String(logConfigName);
            if (doc != null) {
                propertyConfig.load(doc);
            }
        }

        Map<String, String> tinylogConfig = propertyConfig.getSubMap("tinylog.");

        Configuration.replace(tinylogConfig);


        //初始化bean
        List<String> beanConfigNames = new ArrayList<>();
        beanConfigNames.add("BeanConfig.xml");
        if (environment != null) {
            beanConfigNames.add("%s_BeanConfig.xml".formatted(environment));
        }
        for (String beanConfigFileName : beanConfigNames) {
            String doc = readFile2String(beanConfigFileName);
            if (doc != null) {
                Config.initConfig(doc);
            }
        }

    }


    private static String readFile2String(String configFileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(configFileName)) {
            if (is == null) {
                System.out.println("Resource NOT found: " + configFileName);
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
