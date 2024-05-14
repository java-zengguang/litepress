package com.zg.common.util.reflect;

import com.zg.common.annotation.AutoIncrease;
import com.zg.common.annotation.PrimaryKey;
import com.zg.common.bean.handle.TransHandler;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransEntityTypeUtils {
    public static String dateFormat = "yyyy-MM-dd HH:mm:ss";
    public static SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    public static Object translateType(String value, Class type) {
        String fieldType = type.getSimpleName();
        return translateType(value, fieldType);
    }

    public static Object translateType(String value, String fieldType) {

        Object object = null;

        switch (fieldType) {
            case "int": {
                object = Integer.valueOf(value);
                break;
            }
            case "Integer": {
                object = Integer.valueOf(value);
                break;
            }
            case "String": {
                object = value;
                break;
            }
            case "long": {
                object = Long.valueOf(value);
                break;
            }
            case "BigDecimal": {
                object = new BigDecimal(value);
                break;
            }
            case "boolean": {
                object = Boolean.valueOf(value);
                break;
            }
            case "Date": {

                object = sdf.format(value);
                break;
            }
            default: {

                object = value;
                break;
            }

        }
        return object;
    }

    public static boolean isPrimitive(Class type) {
        // Class type = object.getClass();
        if (type.isPrimitive()) {
            return true;
        }
        if (type == Integer.class) {
            return true;
        }
        if (type == Boolean.class) {
            return true;
        }
        if (type == String.class) {
            return true;
        }
        if (type == Date.class) {
            return true;
        }
        return type == BigDecimal.class;
    }

    public static Object translateNull(Object o) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = o.getClass().getFields();
        for (Field field : fields) {
            if (String.valueOf(field.get(o)).equals(""))
                field.set(o, "null");
        }
        return o;
    }




}
