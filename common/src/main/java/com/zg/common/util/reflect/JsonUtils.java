package com.zg.common.util.reflect;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonUtils {
    public static JSON objectToJson(Object obj) throws IllegalArgumentException, IllegalAccessException {
        return (JSON) JSONObject.toJSON(obj);
    }

    public static Object jsonToObject(String json, Class classes) {

        return JSON.parseObject(json, classes);
    }

    public static Object jsonToObject(String json, TypeReference typeReference) {

        return JSON.parseObject(json, typeReference);
    }

    public static Map jsonToObject(String json) {

        return (Map) JSON.parse(json);
    }


    public static String objectToJsonString(Object object) {
        return JSON.toJSONString(object);
    }

    public static Object deepCopy(Object obj) {
        return JSONObject.parseObject(JSONObject.toJSONString(obj), obj.getClass());
    }

    public static List deepCopyList(List originList) {
        List targetList= (List) originList.stream().map(x->deepCopy(x)).collect(Collectors.toList());
        return targetList;
    }

}



