package com.zg.common.init;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationCache {

    private static final Map<Class, Set<Class<?>>> annotationMap = new ConcurrentHashMap();  //注解映射

    public static void set(Class clazz, Set<Class<?>> classSet) {
        if (clazz != null && classSet != null) {
            if (annotationMap.containsKey(clazz)) {
                annotationMap.get(clazz).addAll(classSet);
            } else {
                annotationMap.put(clazz, classSet);
            }
        }
    }

    public static Set<Class<?>> get(Class clazz) {
        return annotationMap.get(clazz);
    }

}
