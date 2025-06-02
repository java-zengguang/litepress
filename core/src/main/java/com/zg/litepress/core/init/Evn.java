package com.zg.litepress.core.init;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.tinylog.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Evn {
    private static String rootPath;
    private static String dynamicModelPath;
    private static String modulePath;
    private static String beanConfig;
    private static Document document;

    private static String environment; //环境

    private static Map<String,String>  initConfigMap=new HashMap<>();


    public static String getDynamicModelPath() {
        return dynamicModelPath;
    }

    public static void setDynamicModelPath(String dynamicModelPath) {
        Evn.dynamicModelPath = dynamicModelPath;
    }

    public static void setRootPath(String rootPath) throws DocumentException {
        Evn.rootPath = rootPath;
        Evn.modulePath = rootPath;

    }

    public static void putInitConfigMap(String key,String value){
        initConfigMap.put(key,value);
    }

    public static String getInitConfigMap(String key){
        return initConfigMap.get(key);
    }

    public static String getRootPath() {
        return rootPath;
    }

    public static String getModulePath() {
        return modulePath;
    }

    public static void setModulePath(String modulePath) {
        Evn.modulePath = modulePath;
    }


    public static void setBeanConfig(String beanConfig) {
        Evn.beanConfig = beanConfig;
    }

    private static void createDocument() throws DocumentException, IOException {
        InputStream inputStream = new ByteArrayInputStream(beanConfig.getBytes(StandardCharsets.UTF_8));
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("读取配置文件失败");
        } finally {
            inputStream.close();
        }
    }

    public static Element getRootElement() {
        try {
            if (document == null) {
                createDocument();
            }
            return document.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEnvironment() {
        return environment;
    }

    public static void setEnvironment(String environment) {
        Evn.environment = environment;
    }
}
