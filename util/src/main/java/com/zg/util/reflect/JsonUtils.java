package com.zg.util.reflect;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonUtils {
    public static JSON objectToJson(Object obj) throws IllegalArgumentException, IllegalAccessException {
        return (JSON) JSONObject.toJSON(obj);
    }

    public static Object jsonToObject(String json, Class classes) {

        Object object = JSON.parseObject(json, classes);
        return object;
    }

    public static String objectToJsonString(Object object) {
        return JSON.toJSONString(object);
    }

}



