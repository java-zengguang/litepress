package com.zg.util.reflect;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {
    public static JSON objectToJson(Object obj) throws IllegalArgumentException, IllegalAccessException {
        if(obj==null){
            return null;
        }else {
            JSONObject jo = new JSONObject();
           if (obj instanceof Collection) {
                return objectCollectionToJson((Collection) obj);
            } else if (obj instanceof Map) {
                return mapToJson((Map) obj);
            } else {
                Field fields[] = obj.getClass().getFields();
                for (Field f : fields) {
                    jo.put(f.getName(), FieldUtils.getFieldJSON(f, obj));
                    if (!FieldUtils.isPrimitive(f)) {
                        if (FieldUtils.isMap(f)) {
                            jo.element(f.getName(), mapToJson((Map) obj));
                        } else if (FieldUtils.isCollection(f)) {
                            JSON jso = objectCollectionToJson((Collection) f.get(obj));
                            jo.element(f.getName(), jso);
                        } else {
                            JSON jso = objectToJson(f.get(obj));
                            jo.element(f.getName(),jso);
                        }
                    }
                }
                return jo;
            }
        }
    }

    public static JSONArray objectCollectionToJson(Collection collection) throws IllegalArgumentException, IllegalAccessException {
        if (collection == null) {
            return null;
        } else {
            JSONArray jsa = new JSONArray();
            for (Object obj : collection) {
                JSON jso = objectToJson(obj);
                jsa.add(jso);
            }
            return jsa;
        }
    }

    //map转化成json,用于封装josn对象
    public static JSON mapToJson(Map map) throws IllegalAccessException {

        if (map == null) {
            return null;
        } else {
            JSONObject json = new JSONObject();
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                Object value = map.get(key);
                if (value instanceof Map) {
                    json.element(key, mapToJson((Map) value));
                } else if (FieldUtils.isPrimitive(value)) {
                    json.element(key, value);
                } else {
                    json.element(key, objectToJson(value));
                }

            }
            return json;
        }
    }



    public static List<String> getJsonArray(String msg){

        List<String> list=new ArrayList<String>();
        Pattern p = Pattern.compile("(\\[[^\\]]*\\])");
        Matcher m = p.matcher(msg);
        while(m.find()){
            list.add(m.group().substring(1, m.group().length()-1));
        }
        return list;
    }


/*    private static Map<String,String> jsonToMap(String json){
        int cont=0;
        Map<String,String> jsonMap=new HashMap();
        String regex = "\\{([^}]*)\\}";
        Pattern pattern = Pattern.compile (regex);
        Matcher matcher = pattern.matcher ("你需要匹配的字符串");
        while (matcher.find ())
        {
            System.out.println (matcher.group ());
            if(cont==0){
                jsonMap.put("mainJsonObject",matcher.group());
            }else{
                jsonMap.put("paramterJsonObject"+cont,matcher.group());
                String s=jsonMap.get("mainJsonObject").replace(matcher.group(),"paramterJsonObject"+cont);
            }

            //jsonMap.put("",matcher.group());
        }

        System.out.println(jsonMap);
        return jsonMap;
    }*/


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

    public static Object jsonToModel(String json, Class classes) throws IllegalAccessException, InstantiationException {

        Object object =classes.newInstance();
        Field fields[]=classes.getFields();
        String strs[]=json.split(",");
        Map<String,String> entryMap=new HashMap();
        for(String str:strs){
            String entry[]=str.split(":");
            entryMap.put(entry[0].replace("\'","").replace("\"",""),entry[1].replace("\'","").replace("\"",""));
        }
        for(Field field:fields){
            FieldUtils.setField(field,object,entryMap.get(field.getName()));
        }
        return object;
    }




    public static Object jsonToObject(String json, Class classes) throws IllegalAccessException, InstantiationException {
        Map<String,Object> jsonMap=  jsonToMap(json,"");
        Object object=mapToModel(jsonMap,classes);
        return object;
    }



    public static Object mapToModel(Map<String, Object> map, Class classes) throws IllegalAccessException, InstantiationException {

        Field[] fields=classes.getFields();
        Object object=classes.newInstance();
        for(Field field:fields){
            String fieldName=field.getName().trim();
            if(FieldUtils.isPrimitive(field)){
                Object value=map.get(fieldName);
                if(value!=null && value instanceof Map ){
                    value=JsonUtils.mapToJson((Map)value).toString();
                }
                FieldUtils.setField(field,object, (String) value);
            }else{
                Class type=field.getType();
                Object value=mapToModel((Map<String, Object>) map.get(field.getName()),type);
                field.set(object,value);
            }
        }
        return object;
    }

}






/*	private static JSON elementJson(String key,Object value,JSONObject json){
        if(value instanceof Map){
			json.element(key,(Map) value);
		}
		if(value instanceof Collection){
			json.element(key,(Collection) value);
		}
		if(value instanceof String){
			json.element(key,(String )value);
		}
		if(value instanceof Integer){
			json.element(key,(int ) value);
		}
		if(value instanceof Long){
			json.element(key,(long ) value);
		}
		if(value instanceof Double){
			json.element(key,(double ) value);
		}
		if(value instanceof Float){
			json.element(key,(float ) value);
		}
		if(value instanceof Character){
			json.element(key,(char ) value);
		}
		if(value instanceof Short){
			json.element(key,(short ) value);
		}
		if(value instanceof Byte){
			json.element(key,(byte ) value);
		}

		return json;

	}*/
