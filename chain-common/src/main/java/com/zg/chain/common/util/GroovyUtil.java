package com.zg.chain.common.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroovyUtil {

    public static List dealObjeList(List list, Map<String, String> shellMap, Map<String, Object> paraMap) throws IOException, NoSuchFieldException, IllegalAccessException {
        List resultList = new ArrayList();
        for (Object obj : list) {
            obj = dealObj(obj, shellMap, paraMap);
            resultList.add(obj);
        }
        return resultList;
    }

    public static Object dealObj(Object obj, Map<String, String> shellMap, Map<String, Object> paraMap) throws CompilationFailedException, IOException, IllegalAccessException, NoSuchFieldException {
        Binding binding = new Binding();
        Class classes = obj.getClass();
        List<Field> fields = Arrays.asList(classes.getFields());

        for (Field field : fields) {
            binding.setVariable(field.getName(), field.get(obj));//将对象值绑定groovy脚本，后面用来解析
        }

        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            binding.setVariable(entry.getKey(), entry.getValue());//绑定自定义参数
        }

        GroovyShell groovyShell = new GroovyShell(binding);

        for (Map.Entry<String, String> shellEntry : shellMap.entrySet()) {
            Object value = groovyShell.evaluate(shellEntry.getValue());
            List<Field> subList = fields.stream().filter(a -> shellEntry.getKey().equals(a.getName())).collect(Collectors.toList());
            if (subList != null && subList.size() > 0) {
                Field field = subList.get(0);
                Object fieldValue = field.get(obj);
                //处理精度问题
                if (fieldValue instanceof BigDecimal && value instanceof BigDecimal) {
                    int scale = ((BigDecimal) fieldValue).precision();
                    ((BigDecimal) value).setScale(scale, RoundingMode.HALF_UP);  //使用四舍五入
                }
                field.set(obj, value);  //将脚本执行后的值放到对象中
                groovyShell.setVariable(field.getName(), value); //按顺序滚动计算
            }

        }
        return obj;
    }


    public static Object dealFieldObj(Object obj, String shell) throws CompilationFailedException, IOException, IllegalAccessException, NoSuchFieldException {
        Binding binding = new Binding();
        Class classes = obj.getClass();
        Field[] fields = classes.getFields();
        for (Field field : fields) {
            binding.setVariable(field.getName(), field.get(obj));//将对象值绑定groovy脚本，后面用来解析
        }
        GroovyShell groovyShell = new GroovyShell(binding);

        return groovyShell.evaluate(shell);


    }

}


