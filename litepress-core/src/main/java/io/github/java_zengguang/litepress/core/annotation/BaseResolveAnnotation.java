package io.github.java_zengguang.litepress.core.annotation;


import io.github.java_zengguang.litepress.core.init.AnnotationCache;
import org.apache.commons.collections.map.HashedMap;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/12/3 0003.
 */
public abstract class BaseResolveAnnotation {


    public Map<String, Object> resovleAnnoation(Class annotationClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException, UnknownHostException {
        return getAnnotationClass( annotationClass);
    }

    public Map<String, Object> getAnnotationClass( Class annotationClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnknownHostException {

        Map<String, Object> resultMap = new HashedMap();
        Set<Class<?>> classList = AnnotationCache.get(annotationClass);
        for (Class classes : classList) {
            Object annotationObject = classes.getAnnotation(annotationClass);
            if (annotationObject != null) {
                resultMap.put(getResultName(annotationObject), getResultValue(classes));
            }

        }
        return resultMap;
    }

    public abstract String getResultName(Object annotationObject) throws IllegalAccessException, InstantiationException;

    public abstract Object getResultValue(Class classes) throws UnknownHostException;


}
