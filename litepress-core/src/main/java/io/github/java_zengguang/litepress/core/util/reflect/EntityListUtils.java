package io.github.java_zengguang.litepress.core.util.reflect;

import io.github.java_zengguang.litepress.core.annotation.AutoIncrease;
import io.github.java_zengguang.litepress.core.annotation.PrimaryKey;
import io.github.java_zengguang.litepress.core.bean.handle.TransHandler;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class EntityListUtils {


    public static <T extends Serializable> List<T> deepClone(List<T> originalList) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(originalList);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (List<T>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    //这里弃用hash的方式，数据量大会出现重复，导致对比不准，改用String映射的方式，降低重复率
    public static Map<String, Object> transToPKMap(Collection list) throws IllegalAccessException {
        Map<String, Object> resultMap = new HashMap<>();
        for (Object obj : list) {
            Class modelClass = obj.getClass();
            String pkHashCode = "";
            Field[] fields = modelClass.getFields();
            for (Field field : fields) {
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    Object value = field.get(obj);
                    if (value instanceof String) {
                        pkHashCode = pkHashCode + value;
                    } else {
                        pkHashCode = pkHashCode + Objects.hashCode(value);
                    }

                }
            }
            resultMap.put(pkHashCode, obj);
        }
        return resultMap;

    }




    public static List trans2MapList(List dataList, TransHandler transHandler) {
        List list = new ArrayList<>();
        dataList.forEach(x -> {
            list.add(transHandler.trans(x));
        });
        return list;
    }

    public static Map getPKDataMap(Object obj) throws IllegalAccessException {
        Map<String, Object> hashMap = new HashMap();
        Class modelClass = obj.getClass();
        Field[] fields = modelClass.getFields();
        for (Field field : fields) {
            PrimaryKey primaryKeyField = field.getAnnotation(PrimaryKey.class);
            if (primaryKeyField != null) {
                hashMap.put(field.getName(), field.get(obj));
            }
        }

        return hashMap;

    }


    public static List<String> getIncreaseFields(Class modelClass) {

        List<String> list = new ArrayList();

        Field[] fields = modelClass.getFields();

        for (Field field : fields) {
            AutoIncrease notCommitField = field.getAnnotation(AutoIncrease.class);
            if (notCommitField != null) {
                list.add(field.getName());
            }
        }

        return list;

    }

    public static List getPKData(Object obj) throws IllegalAccessException {

        List list = new ArrayList();
        Class modelClass = obj.getClass();
        Field[] fields = modelClass.getFields();

        for (Field field : fields) {
            PrimaryKey primaryKeyField = field.getAnnotation(PrimaryKey.class);
            if (primaryKeyField != null) {
                list.add(field.get(obj));
            }
        }

        return list;

    }



}
