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









    private static Map<String,Object> jsonToMap(String bson, String bsonName)  {

        Map map=new HashMap<>();
        int start = bson.indexOf("{")+1;
        int end = bson.lastIndexOf("}");
        String json=bson.substring(start,end);
        if(json.contains("{") && json.contains("}")) {
            String subString=json.substring(0,json.indexOf("{"));
            int nameStart=subString.lastIndexOf(",");
            int nameEnd=subString.lastIndexOf(":");
            String name=subString.substring(nameStart+1,nameEnd).replace("\"","");
            map.put(name, jsonToMap(json,name));
            json=json.substring(0,nameStart)+json.substring(json.indexOf("}")+1,json.length());
        }
        if(!json.contains("{") || !json.contains("}")){
            map.putAll(jsonToMap(json));
        }
        System.out.println("map"+map);
        return map;
    }


    public static Map<String,String> jsonToMap(String json) {


        String strs[]=json.split(",");
        Map<String,String> entryMap=new HashMap();
        for(String str:strs){
            String entry[]=str.split(":");
            entryMap.put(entry[0].replace("\'","").replace("\"",""),entry[1].replace("\'","").replace("\"",""));
        }

        return entryMap;
    }






    public static Object jsonToObject(String json, Class classes) {

        Object object = JSON.parseObject(json, classes);
        return object;
    }

    public static String objectToJsonString(Object object){
        return JSON.toJSONString(object);
    }



}



