package com.zg.common.util.reflect;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.tinylog.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


@SuppressWarnings({"unchecked"})
public class JsonUtil {


    /**
     * ObjectMapper线程安全
     */
    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //  查询时间数据 数据库和返回数据相差8个小时的问题
        TimeZone zone = TimeZone.getTimeZone("Asia/Shanghai");
        if (null == zone) {
            zone = TimeZone.getTimeZone("GMT+8");
        }
        TimeZone.setDefault(zone);
        // 指定时区
        objectMapper.setTimeZone(zone);
        DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
        dateFormat.setLenient(true);
        dateFormat.setTimeZone(zone);
        objectMapper.setDateFormat(dateFormat);
    }


    /**
     * 对象转Json格式字符串
     *
     * @param obj 对象
     * @return Json格式字符串
     */
    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            Logger.info(e.getMessage());
            return null;
        }
    }


    public static void setNullValueSerializer(ObjectMapper ome) {
        ome.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString("");
            }
        });
    }

    /**
     * 字符串转换为自定义对象
     *
     * @param str   要转换的字符串
     * @param clazz 自定义对象的class对象
     * @return 自定义对象
     */
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (str == null || "".equals(str) || clazz == null) {
            return null;
        }
        try {
            return string2Obj(str, clazz, false);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<T> clazz, boolean throwFlag) throws Exception {
        if (str == null || "".equals(str) || clazz == null) {
            return null;
        }
        try {
            T t;
            if (clazz.equals(String.class)) {
                t = (T) str;
            } else if (clazz.equals(JsonNode.class) || clazz.equals(ObjectNode.class)) {
                str = replace(str);
                t = (T) objectMapper.readTree(str);
            } else {
                t = objectMapper.readValue(str, clazz);
            }
            return t;
        } catch (Exception e) {
            if (throwFlag) {
                throw e;
            }
            Logger.info(e.getMessage());
            return null;
        }
    }

    /**
     * TODO 修复feign调用时对JSON数据的多增加双引号导致的解析异常
     */
    private static String replace(String str) {
        if (str.startsWith("\"\"{") && str.endsWith("}\"\"")) {
            str = str.replace("\"\"{", "{").replace("}\"\"", "}");
        }
        if (str.startsWith("\"{") && str.endsWith("}\"")) {
            str = str.replace("\"{", "{").replace("}\"", "}");
        }
        return str;
    }

    /**
     * 字符串转换为集合对象(List Map等)
     *
     * @param str           要转换的字符串
     * @param typeReference 集合对象 示例new TypeReference<List<YourBean>>(){}
     * @return 集合对象
     */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (str == null || "".equals(str) || null == typeReference) {
            return null;
        }
        try {
            return string2Obj(str, typeReference, false);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T string2Obj(String str, TypeReference<T> typeReference, boolean throwFlag) throws Exception {
        if (str == null || "".equals(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (IOException e) {
            if (throwFlag) {
                throw e;
            }
            Logger.info(e.getMessage());
            return null;
        }
    }


    public static <T> T string2Obj(String str, Class<?> collectionClazz, Class<?>... elementClazzes) {
        if (str == null || "".equals(str) || collectionClazz == null) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            Logger.info(e.getMessage());
            return null;
        }
    }


}

