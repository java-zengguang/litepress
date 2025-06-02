package com.zg.litepress.chain.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class ProessTransUtil {


    //屏蔽大小写
    //把 Object 的属性转到  普通entity中，便于操作数据库, 可用于 Proccess 、ProccesBatch 到普通Entity的转换，方便存储数据库，避免字段重用的问题
    public static Object transProcess2Model(Object process, Class tClasses) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Object tObject = tClasses.getDeclaredConstructor().newInstance();
        Field[] tFields = tClasses.getFields();
        Class pClass = process.getClass();
        Field[] pFields = pClass.getFields();
        Map pFieldMap = stream(pFields).map(field -> asList(field.getName().toUpperCase(), field)).collect(Collectors.toMap(a -> a.get(0), a -> a.get(1)));
        for (Field tField : tFields) {
            String tFieldName = tField.getName().toUpperCase();
            if (pFieldMap.containsKey(tFieldName)) {
                Field pField = (Field) pFieldMap.get(tFieldName);
                Object pValue = pField.get(process);
                tField.set(tObject, pValue);
            }
        }
        return tObject;
    }


    public static List transProcess2ModelList(List processList, Class tClasses) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        List tList = new ArrayList();
        for (Object obj : processList) {
            Object tObject = transProcess2Model(obj, tClasses);
            tList.add(tObject);
        }
        return tList;
    }


}
