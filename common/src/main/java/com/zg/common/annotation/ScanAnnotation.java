package com.zg.common.annotation;


import com.zg.common.util.CommonUtil;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/12/3 0003.
 */
public class ScanAnnotation {


    private static Map<Class, Set<Class<?>>> annotationMap;


    public static void scanModule(Module module) {
        if (annotationMap != null) {
            return;
        }

        annotationMap = new HashMap<>();
        Set<Class<?>> moduleClassSet = scanModuleClass(module);


        for (Class classes : moduleClassSet) {
            if (!classes.isAnnotation()) {
                Annotation[] annotations = classes.getAnnotations();
                for (Annotation annotation : annotations) {
                    Class annotationType = annotation.annotationType();
                    Set<Class<?>> classSet = annotationMap.get(annotationType);
                    if (classSet == null) {
                        classSet = new HashSet<>();
                        annotationMap.put(annotationType, classSet);
                    }
                    if (classes.getAnnotation(annotationType) != null) {
                        classSet.add(classes);
                    }
                }
            }
        }
        System.out.println(annotationMap);
    }


    private static Set<Class<?>> scanModuleClass(Module module) {
        Set<String> packages = module.getPackages();
/*        Set<String> subPackages = new HashSet<>();
        for (String s:packages){
            if(s.contains("com")){
                subPackages.add(s);
            }
        }*/
        Set<Class<?>> classeSet = new HashSet<>();
        for (String aPackage : packages) {
            classeSet.addAll(CommonUtil.getClasses(aPackage));
        }
        return classeSet;
    }


    public static Set<Class<?>> getClassFromAnn(Class annotation) {
        return annotationMap.get(annotation);
    }

    public static void main(String args[]) {
        ScanAnnotation.scanModule(ScanAnnotation.class.getModule());
        System.out.println(ScanAnnotation.getClassFromAnn(Model.class));
    }
}
