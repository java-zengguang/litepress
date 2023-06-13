package com.zg.common.util.reflect;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.Map;

public class JsonUtils {
    public static JSON objectToJson(Object obj) throws IllegalArgumentException, IllegalAccessException {
        return (JSON) JSONObject.toJSON(obj);
    }

    public static Object jsonToObject(String json, Class classes) {

        Object object = JSON.parseObject(json, classes);
        return object;
    }

    public static Object jsonToObject(String json, TypeReference typeReference) {

        Object object = JSON.parseObject(json, typeReference);
        return object;
    }

    public static Map jsonToObject(String json) {

        Map map = (Map) JSON.parse(json);

        return map;
    }


    public static String objectToJsonString(Object object) {
        return JSON.toJSONString(object);
    }

    public static Object deepCopy(Object obj){
       return JSONObject.parseObject(JSONObject.toJSONString(obj),obj.getClass());
    }

}



