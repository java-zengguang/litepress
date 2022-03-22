package com.zg.util.reflect;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {
    public static JSON objectToJson(Object obj) throws IllegalArgumentException, IllegalAccessException {
       return (JSON) JSONObject.toJSON(obj);
    }

    public static Object jsonToObject(String json, Class classes) {

        Object object = JSON.parseObject(json, classes);
        return object;
    }

    public static String objectToJsonString(Object object){
        return JSON.toJSONString(object);
    }

}



