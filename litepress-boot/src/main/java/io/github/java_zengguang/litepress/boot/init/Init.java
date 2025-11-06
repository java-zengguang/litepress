package io.github.java_zengguang.litepress.boot.init;


import io.github.java_zengguang.litepress.boot.annotation.ProjectRootPath;
import io.github.java_zengguang.litepress.boot.annotation.ScanPackages;
import io.github.java_zengguang.litepress.boot.annotation.TargetEvn;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.core.init.Evn;
import io.github.java_zengguang.litepress.core.relect.dynameic.DynamicClass;
import org.dom4j.DocumentException;
import org.tinylog.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Init {
    public static boolean init = true;

    private static void initAnnotation(Class clazz) {

        ScanPackages scanPackages = (ScanPackages) clazz.getAnnotation(ScanPackages.class);
        String packages = scanPackages.value();
        System.out.println("扫描路径" + packages);
        if (true) { //扫描controller
            PackageScan.scanByAnnotations(packages.split(","));
        }

    }



    public static void loadTinyLogConfig() {

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


    public static void doMain(Class clazz) {  //启动类
        System.out.println("初始化");
        if (init) {
            try {
                initEvn(clazz);
                loadTinyLogConfig();
                initBean();
                initAnnotation(clazz);
                init = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String readFile2String(String configFileName) throws IOException {
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
            if (doc == null) {
                return;
            }
            System.out.println(doc);
            Evn.putInitConfigMap("tinyLogConfig", doc);
        } catch (IOException e) {
            System.out.println(configFileName + " 加载失败！");
        }
    }


    private static void initBean() {
        Config.initConfig();
    }

    private static void readBeanConfig() throws IOException {

        String configFileName = "BeanConfig.xml";
        if (Evn.getEnvironment() != null) {
            configFileName = Evn.getEnvironment() + "_" + configFileName;
        }
        System.out.println("加载配置文件" + configFileName);
        String doc = readFile2String(configFileName);
        System.out.println(doc);
        Evn.setBeanConfig(doc);

    }


    private static String getPath(Class clazz) {
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(path);
        path = file.getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        path = path + File.separator;
        return path;
    }

    private static void initEvn(Class clazz) throws DocumentException, IOException {
        System.out.println("初始化环境变量");
        TargetEvn targetEvn = (TargetEvn) clazz.getAnnotation(TargetEvn.class);
        Evn.setEnvironment(targetEvn.value());

        //使用环境变量覆盖，兼容方式
        String sTargetEvn = System.getenv("TargetEvn");
        if (sTargetEvn != null) {
            Evn.setEnvironment(sTargetEvn);
        }

        ProjectRootPath projectRootPath = (ProjectRootPath) clazz.getAnnotation(ProjectRootPath.class);
        Evn.setRootPath(projectRootPath.value());
        System.out.println(Evn.getRootPath());

        Evn.setModulePath(getPath(clazz));
        System.out.println(Evn.getModulePath());

        Evn.setDynamicModelPath(getPath(DynamicClass.class));
        System.out.println(Evn.getDynamicModelPath());

        readLogConfig();
        readBeanConfig();
    }


}
