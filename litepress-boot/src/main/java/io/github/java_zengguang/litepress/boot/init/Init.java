package io.github.java_zengguang.litepress.boot.init;


import io.github.java_zengguang.litepress.boot.annotation.ScanPackages;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.core.init.Evn;
import org.tinylog.configuration.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Init {




    public static void initEvn() throws IOException {
        //使用环境变量覆盖，兼容方式
        String sTargetEvn = System.getenv("TargetEvn");
        if (sTargetEvn == null) {
            sTargetEvn = "dev";
        }
        Evn.setEnvironment(sTargetEvn);
        readLogConfig();
        readBeanConfig();
    }


    private static String readFile2String(String configFileName) throws IOException {
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

    private static void readLogConfig() {
        String configFileName = "tinylog.properties";
        if (Evn.getEnvironment() != null) {
            configFileName = Evn.getEnvironment() + "_" + configFileName;
        }
        try {
            System.out.println("加载配置文件" + configFileName);
            String doc = readFile2String(configFileName);
            System.out.println(doc);
            Properties properties = new Properties();
            properties.load(new StringReader(doc));
            properties.forEach((key, value) -> {
                Configuration.set((String) key, (String) value);
            });

        } catch (IOException e) {
            System.out.println(configFileName + " 加载失败！");
        }
    }


    private static void readBeanConfig() throws IOException {

        String configFileName = "BeanConfig.xml";
        if (Evn.getEnvironment() != null) {
            configFileName = Evn.getEnvironment() + "_" + configFileName;
        }
        System.out.println("加载配置文件" + configFileName);
        String doc = readFile2String(configFileName);
        System.out.println(doc);
        Config.initConfig(doc);

    }


}
