package io.github.java_zengguang.litepress.boot.init;


import io.github.java_zengguang.litepress.boot.annotation.ProjectRootPath;
import io.github.java_zengguang.litepress.boot.annotation.ScanPackages;
import io.github.java_zengguang.litepress.boot.annotation.TargetEvn;
import io.github.java_zengguang.litepress.core.init.Evn;
import io.github.java_zengguang.litepress.core.relect.dynameic.DynamicClass;
import org.dom4j.DocumentException;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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


    public static void doMain(Class clazz) {  //启动类
        System.out.println("初始化");
        if (init) {
            try {
                initEvn(clazz);
                initConfig(clazz);
                initLogConfig(clazz);
                initAnnotation(clazz);
                init = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String getConfigData(String configFileName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        StringBuffer stringBuffer = new StringBuffer();
        try (InputStream is = classLoader.getResourceAsStream(configFileName)) {
            if (is == null) {
                System.out.println("Resource NOT found: " + configFileName);
                return null;
            }
            // 使用 BufferedReader 读取文件内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    private static void initLogConfig(Class clazz) {
        String configFileName = "tinylog.properties";
        if (Evn.getEnvironment() != null) {
            configFileName = Evn.getEnvironment() + "_" + configFileName;
        }
        try {
            System.out.println("加载配置文件" + configFileName);
            String doc = getConfigData(configFileName);
            if (doc == null) {
                return;
            }
            System.out.println(doc);
            Evn.putInitConfigMap("tinyLogConfig", doc);
        } catch (IOException e) {
            System.out.println(configFileName + " 加载失败！");
        }
    }

    private static void initConfig(Class clazz) throws IOException {

        String configFileName = "BeanConfig.xml";
        if (Evn.getEnvironment() != null) {
            configFileName = Evn.getEnvironment() + "_" + configFileName;
        }
        System.out.println("加载配置文件" + configFileName);
        String doc = getConfigData(configFileName);
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

    private static void initEvn(Class clazz) throws DocumentException {
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


    }


}
