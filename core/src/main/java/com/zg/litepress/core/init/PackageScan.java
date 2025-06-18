package com.zg.litepress.core.init;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

public class PackageScan {


    public static Set<Class<?>> scanByClassType(String[] packages, Class clazz) {
        // 创建ConfigurationBuilder并设置扫描范围
        ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.forPackages(packages) // 扫描指定包
                .setScanners(new SubTypesScanner(false)); // false意味着不包括Object类
        // 创建Reflections实例并执行扫描
        Reflections reflections = new Reflections(configuration);
        Set<Class<?>> subTypes = reflections.getSubTypesOf(clazz);
        return subTypes;
    }

    public static Set<Class<?>> scanByAnnotation(String[] packages, Class<? extends Annotation> annotation ) {
        // 创建ConfigurationBuilder并设置扫描范围
        ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.forPackages(packages) // 扫描指定包
                .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()); // 添加TypeAnnotationsScanner
        // 创建Reflections实例并执行扫描
        Reflections reflections = new Reflections(configuration);
        Set<Class<?>> subTypes = reflections.getTypesAnnotatedWith(annotation);
        return subTypes;

    }

    public static void scanByAnnotations(String[] packages ) {

        // 创建ConfigurationBuilder并设置扫描范围
        ConfigurationBuilder configuration = new ConfigurationBuilder();
        configuration.forPackages(packages) // 扫描指定包
                .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()); // 添加TypeAnnotationsScanner
        // 创建Reflections实例并执行扫描
        Reflections reflections = new Reflections(configuration);
        Map<String, Set<String>> typesAnnotated= reflections.getStore().get("TypesAnnotated");
        typesAnnotated.forEach((key,val)->{
            Set<Class<?>> classSet=val.stream().map((name)-> {
                try {
                    return Class.forName(name);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toSet());
            try {
               Class annotation= Class.forName(key);
                AnnotationCache.set(annotation, classSet);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });


    }


}
