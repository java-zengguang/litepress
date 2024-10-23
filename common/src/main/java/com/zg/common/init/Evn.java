package com.zg.common.init;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Evn {
    private static String rootPath;
    private static String modulePath;
    private static String beanConfig;
    private static Document document;

    private static String environment; //环境


    public static void setRootPath(String rootPath) throws DocumentException {
        Evn.rootPath = rootPath;
        Evn.modulePath = rootPath;

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
            System.out.println("读取配置文件失败");
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
