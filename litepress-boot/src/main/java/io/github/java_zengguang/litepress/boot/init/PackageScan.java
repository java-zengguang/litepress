package io.github.java_zengguang.litepress.boot.init;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.java_zengguang.litepress.core.init.AnnotationCache;
import org.tinylog.Logger;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PackageScan {


    public static Set<Class<?>> scanByClassType(String[] packages, Class clazz) {
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(packages) // 只扫描指定包
                .enableAnnotationInfo()   // 启用注解扫描
                .enableClassInfo()        // 启用类信息扫描
                .ignoreClassVisibility() // 可选：包括非public类
                .scan()) {               // 执行扫描
            Set<Class<?>> classSet = scanResult.getSubclasses(clazz).stream().map((classInfo) ->
                    classInfo.loadClass()
            ).collect(Collectors.toSet());
            return classSet;
        }

    }

    public static Set<Class<?>> scanByAnnotation(String[] packages, Class<? extends Annotation> annotation) {

        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(packages) // 只扫描指定包
                .enableAnnotationInfo()   // 启用注解扫描
                .enableClassInfo()        // 启用类信息扫描
                .ignoreClassVisibility() // 可选：包括非public类
                .scan()) {               // 执行扫描
            Set<Class<?>> classSet = scanResult.getClassesWithAnnotation(annotation).stream().map((classInfo) ->
                    classInfo.loadClass()
            ).collect(Collectors.toSet());
            return classSet;
        }
    }


    public static void scanByAnnotations(String[] packages) {
        Map<Class<?>, Set<Class<?>>> annotationByClassMap = new HashMap<>();
        try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(packages) // 只扫描指定包
                .enableAnnotationInfo()   // 启用注解扫描
                .enableClassInfo()        // 启用类信息扫描
                .ignoreClassVisibility() // 可选：包括非public类
                .scan()) {               // 执行扫描
            scanResult.getAllClasses().forEach((classInfo) -> {
                classInfo.getAnnotationInfo().forEach((annotationInfo) -> {
                    annotationByClassMap.computeIfAbsent(annotationInfo.getClassInfo().loadClass(), k -> new HashSet<>()).add(classInfo.loadClass());
                });

            });


            annotationByClassMap.forEach((key, val) -> {
                Logger.info(key);
                AnnotationCache.set(key, val);
            });
        }
    }

}
