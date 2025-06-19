package com.zg.groovy.common;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroovyUtil {

    public static List dealObjeList(List list, Map<String, String> shellMap) {
        List resultList = new ArrayList();
        for (Object obj : list) {
            resultList.add(obj);
        }
        return resultList;
    }

    public static Object dealObj(Object obj, Map<String, String> shellMap) throws CompilationFailedException, IOException, IllegalAccessException, NoSuchFieldException {
        Binding binding = new Binding();
        Class classes = obj.getClass();
        Field[] fields = classes.getFields();
        for (Field field : fields) {
            binding.setProperty(field.getName(), field.get(obj));//将对象值绑定groovy脚本，后面用来解析
        }

        GroovyShell groovyShell = new GroovyShell(binding);
        Set<String> keySet = shellMap.keySet();
        for (String key : keySet) {
            Object vlaue = groovyShell.evaluate(shellMap.get(key));
            Field field = classes.getField(key);
            field.set(obj, vlaue);  //将脚本执行后的值放到对象中
        }
        return obj;
    }
}
