package com.zg.common.annotation;


import com.zg.common.util.CommonUtil;
import org.tinylog.Logger;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Administrator on 2018/12/3 0003.
 */
public class ScanAnnotation {


    private static Map<Class, Set<Class<?>>> annotationMap;


    public synchronized static void scanModule(Module module) {
        if (annotationMap == null) {
            annotationMap = new ConcurrentHashMap<>();
        }

        Set<Class<?>> moduleClassSet = scanModuleClass(module);
        for (Class classes : moduleClassSet) {
            if (!classes.isAnnotation()) {
                Annotation[] annotations = classes.getAnnotations();
                for (Annotation annotation : annotations) {
                    Class annotationType = annotation.annotationType();
                    Set<Class<?>> classSet = annotationMap.get(annotationType);
                    if (classSet == null) {
                        classSet = new CopyOnWriteArraySet<>();
                        annotationMap.put(annotationType, classSet);
                    }
                    if (classes.getAnnotation(annotationType) != null) {
                        classSet.add(classes);
                    }
                }
            }
        }
        Logger.info("加载模块注解" + annotationMap);
    }


    private static Set<Class<?>> scanModuleClass(Module module) {
        Set<String> packages = module.getPackages();
        Set<Class<?>> classeSet = new HashSet<>();
        for (String aPackage : packages) {
            classeSet.addAll(CommonUtil.getClasses(aPackage));
        }
        return classeSet;
    }


    public static Set<Class<?>> getClassFromAnn(Class annotation) {
        return annotationMap.get(annotation);
    }


}
