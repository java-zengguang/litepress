package com.zg.util.annotation;

import com.zg.util.io.FileUtils;
import org.apache.commons.collections.map.HashedMap;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/3 0003.
 */
public abstract class BaseResolveAnnotation {


    public  Map<String, Object> resovleAnnoation(String packageName,Class annotationClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
       return getAnnotationClass(packageName,annotationClass);
    }

    public  Map<String, Object> getAnnotationClass(String packageName,Class annotationClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Map<String, Object> resultMap = new HashedMap();
        List<Class> classList = FileUtils.getClassFormPackage(packageName);
        for (Class classes : classList) {
            Object annotationObject=  classes.getAnnotation(annotationClass);
            if (annotationObject != null) {
                resultMap.put(getResultName(annotationObject), getResultValue(classes));
            }

        }
        return resultMap;
    }

    public abstract String getResultName(Object  annotationObject) throws IllegalAccessException, InstantiationException;

    public abstract Object getResultValue(Class classes);


}
