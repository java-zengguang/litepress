package com.zg.mvc.util;

import com.zg.common.annotation.ScanAnnotation;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.common.util.CommonUtil;
import org.apache.commons.collections.map.HashedMap;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/12/3 0003.
 */
public class ResolveAnnotation {


    public static Map<String, Class> resovleController(String packageName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return resovleController(packageName, "");
    }

    public static Map<String, Class> resovleController(String packageName, String rootURL) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (rootURL == null) {
            rootURL = "";
        }
        Map<String, Class> resultMap = new HashedMap();
        Set<Class<?>> classList =  ScanAnnotation.getClassFromAnn(Controller.class);
        for (Class classes : classList) {
            Controller controller = (Controller) classes.getAnnotation(Controller.class);
            if (controller != null) {
                String parentURL = rootURL + controller.value();
                resultMap.put(parentURL, classes);
            }

        }
        return resultMap;
    }

    public static Map<String, Method> resovleResultMap(String parentURI, Class classes) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String subURL = "";
        Map<String, Method> resultMap = new HashedMap();
        Method[] methodArray = classes.getMethods();
        for (Method method : methodArray) {
            ResultMapping annotation = method.getAnnotation(ResultMapping.class);
            if (annotation != null) {
                subURL = annotation.value();
                resultMap.put(parentURI + subURL, method);
            }
        }
        return resultMap;
    }


}
