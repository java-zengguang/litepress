package com.zg.common.util.reflect;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

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


    public static Object translateNull(Object o) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = o.getClass().getFields();
        for (Field field : fields) {
            if (String.valueOf(field.get(o)).equals(""))
                field.set(o, "null");
        }
        return o;
    }


}
