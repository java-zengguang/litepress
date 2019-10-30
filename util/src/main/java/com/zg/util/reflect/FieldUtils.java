package com.zg.util.reflect;

import com.zg.bean.annotation.Model;
import com.zg.bean.annotation.NotCommitField;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */


public class FieldUtils {
    public static String dateFormat = "yyyy-MM-dd HH:mm:ss";
    public static SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    /*    public static Map<String,Map> tableInfoMap=null;*/


    public static void setDateFormat(String dateFormat) {
        FieldUtils.dateFormat = dateFormat;
        FieldUtils.sdf = new SimpleDateFormat(dateFormat);
    }


    //为类的公共属性赋值
    public static void setField(Field field, Object object, String value, String fieldType) throws IllegalArgumentException, IllegalAccessException {

        if (value != null) {

            if ("null".equals(value) || "".equals(value)) {

            } else {
                Object data = translateType(value, fieldType);
                field.set(object, data);
            }
        }
    }

    //为类的公共属性赋值
    public static void setField(Field field, Object object, String value) throws IllegalArgumentException, IllegalAccessException {
        String fieldType = field.getType().getSimpleName();
        setField(field, object, value, fieldType);
    }

    public static String getFieldString(Field field, Object object) throws IllegalArgumentException, IllegalAccessException {
        String value = null;
        String fieldType = field.getType().getSimpleName();
        switch (fieldType) {
            case "int": {
                value = String.valueOf(field.get(object));
                break;
            }
            case "String": {
                value = "'" + String.valueOf(field.get(object)) + "'";
                break;
            }

            case "Date": {
                value = sdf.format(field.get(object));
                break;
            }

         /*   default:{
                value=field.get(object).toString();
            }*/
        }
        if (value == null) {
            value = "";
        }
        return value;
    }

    public static String fieldtoString(Object object){
        String value="";
        String classType=object.getClass().getTypeName();

        switch (classType) {
            case "int": {
                value = String.valueOf(object);
                break;
            }
            case "String": {
                value = object + "";
                break;
            }

            case "Date": {
                value = sdf.format(object);
                break;
            }

            default:{
              break;
            }
        }

        return value;
    }





    public static Object getFieldJSON(Field field, Object object) throws IllegalArgumentException, IllegalAccessException {
        Object value;
        if (field.get(object) == null) {
            value = "null";
        } else {
            String fieldType = field.getType().getSimpleName();
            switch (fieldType) {
                case "int": {
                    value = field.get(object);
                    break;
                }
                case "String": {
                    value = field.get(object);
                    break;
                }

                case "Date": {
                    value = sdf.format(field.get(object));
                    break;
                }

                case "boolean": {
                    value = field.get(object);
                    break;
                }

                default: {
                    value = field.get(object);
                    break;
                }
            }
        }
        if (value == null) {
            value = "";
        }
        return value;
    }

    public static Object translateNull(Object o) throws IllegalArgumentException, IllegalAccessException {
        Field fields[] = o.getClass().getFields();
        for (Field field : fields) {
            if (String.valueOf(field.get(o)).equals("null"))
                field.set(o, "");
        }

        return o;
    }

    public static Object Nulltranslate(Object o) throws IllegalArgumentException, IllegalAccessException {
        Field fields[] = o.getClass().getFields();
        for (Field field : fields) {
            if (String.valueOf(field.get(o)).equals(""))
                field.set(o, "null");
        }

        return o;
    }


    public static Object translateType(String value,Class type){
        String fieldType=type.getSimpleName();
        return translateType(value,fieldType);
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
                        object= Long.valueOf(value);
                        break;
                    }

                    case "boolean":{
                        object= Boolean.valueOf(value);
                        break;
                    }

                    case "Date": {
                        try {
                            object = sdf.parse(value);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    default:{

                        object=value;
                        break;
                    }

        }
        return object;
    }


    //将数据库类型转化为Java类型
    public static String dataTranslateJava(String s) {
        switch (s) {
            case "VARCHAR2":
                s = "String";
                break;

            case "VARCHAR":
                s = "String";
                break;

            case "NUMBER":
                s = "int";
                break;

            case "DATE":
                s = "Date";
                break;

            case "DATETIME":
                s = "Date";
                break;

            default:
                s = s.toLowerCase();
                break;
        }

        return s;
    }



    public static boolean isPrimitive(Field field) {
        Class type = field.getType();
        return isPrimitive(type);
    }

    public static boolean isPrimitive(Object object) {
        return isPrimitive(object.getClass());
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

        return false;
    }




    public static boolean isCollection(Field field) {
        Class type = field.getType();
        if ("List".equals(type.getSimpleName()) || "Set".equals(type.getSimpleName())) {
            return true;
        }
        return false;
    }

    public static boolean isMainModel(Field field) {
        Class type = field.getType().getSuperclass();
        if (type != null && "MainModel".equals(type.getSimpleName())) {
            return true;
        }
        return false;
    }


    public static boolean isMap(Field field) {
        Class type = field.getType();
        return isMap(type);
    }

    public static boolean isMap(Object object) {
        Class type = object.getClass();
        return isMap(type);
    }

    public static boolean isMap(Class type) {
        if ("Map".equals(type.getSimpleName())) {
            return true;
        }
        return false;
    }


    public static int toCompare(Object o1, Object o2, String... terms) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        int fruit = 0;
        for (String term : terms) {

            if (term.contains(".")) {

                String s[] = term.split("\\.");

                Field f = o1.getClass().getField(s[0].trim());
                fruit = toCompare(f.get(o1), f.get(o2), s[1]);
                if (fruit != 0) {
                    return fruit;
                }
            } else {

                Field field = o1.getClass().getField(term);
                Class type = field.getType();


                if (type == int.class) {
                    fruit = field.getInt(o1) - field.getInt(o2);
                    if (fruit != 0) {
                        return fruit;
                    }
                }
                if (type == String.class) {
                    String s1 = (String) field.get(o1);
                    String s2 = (String) field.get(o2);
                    fruit = s1.length() - s2.length();
                    if (fruit != 0) {
                        return fruit;
                    }

                    fruit = ((String) field.get(o1)).compareTo((String) field.get(o2));
                    if (fruit != 0) {
                        return fruit;
                    }

                }

            }

        }
        return fruit;
    }


    public static int toCompare(Object o1, Object o2) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        if (!o1.getClass().equals(o2.getClass())) {
            return -1;
        }
        int fruit = 0;
        for (Field field : o1.getClass().getFields()) {
            Class type = field.getType();

            if (!type.isPrimitive() && type != String.class) {
                fruit = toCompare(field.get(o1), field.get(o2));
                if (fruit != 0) {
                    return fruit;
                }

            } else {

                if (type == int.class) {

                    fruit = field.getInt(o1) - field.getInt(o2);
                    if (fruit != 0) {
                        return fruit;
                    }
                }
                if (type == String.class) {
                    String s1 = (String) field.get(o1);
                    String s2 = (String) field.get(o2);
                    if (s1 == null) {
                        if (s2 == null) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    if (s2 == null) {
                        if (s1 == null) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }

                    fruit = s1.length() - s2.length();
                    if (fruit != 0) {
                        return fruit;
                    }
                    fruit = ((String) field.get(o1)).compareTo((String) field.get(o2));
                    if (fruit != 0) {
                        return fruit;
                    }

                }
            }
        }
        return fruit;
    }



    public static int toCompare(Object o1, Map termMap) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        int fruit = 0;
        Set<String> terms = termMap.keySet();
        for (String term : terms) {

            if (term.contains(".")) {

                String s[] = term.split("\\.");
                Field f = o1.getClass().getField(s[0].trim());
                Map map = new HashMap();
                map.put(s[1], termMap.get(term));
                fruit = toCompare(f.get(o1), map);
                if (fruit != 0) {
                    return fruit;
                }
            } else {


                Field field = o1.getClass().getField(term);
                Class type = field.getType();


                if (type == int.class) {
                    fruit = field.getInt(o1) - Integer.valueOf((String) termMap.get(term));
                    if (fruit != 0) {
                        return fruit;
                    }
                }
                if (type == String.class) {
                    String s1 = (String) field.get(o1);
                    String s2 = (String) termMap.get(term);
                    if(s1==s2){
                        return 0;
                    }
                    if(s1==null || s2==null){
                        return -1;
                    }
                    fruit = s1.length() - s2.length();
                    if (fruit != 0) {
                        return fruit;
                    }
                    fruit = ((String) field.get(o1)).compareTo((String) termMap.get(term));
                    if (fruit != 0) {
                        return fruit;
                    }

                }
            }
        }
        return fruit;
    }


    /*判断object是否在集合中
     * 在则返回true*/
    public static boolean contains(Object object, Collection objectC) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        for (Object o : objectC) {
            if (toCompare(object, o) == 0)
                return true;
        }
        return false;
    }


    public static String getTableNameFromModel(Class modelClass) {
        Model model = (Model) modelClass.getAnnotation(Model.class);
        String tableName = null;
        if (model != null) {
            tableName = model.tableName();
            if (tableName == null || tableName.equals("")) {
                tableName = modelClass.getSimpleName();
            }
        }

        return tableName;
    }


    public static List<String> getNoCommitFields(Class modelClass) {

        List<String> list = new ArrayList();

        Field[] fields = modelClass.getFields();

        for (Field field : fields) {
            NotCommitField notCommitField = field.getAnnotation(NotCommitField.class);
            if (notCommitField != null) {
                list.add(field.getName());
            }
        }

        return list;

    }


    public static String serialize(Object object) throws IllegalAccessException {

            return JsonUtils.objectToJsonString(object);

    }

    public static Object unSerialize(String str,Class classType) throws InstantiationException, IllegalAccessException {
        Object value=null;
        if(isPrimitive(classType)){
            value=translateType(str,classType);
        }else{
            value=JsonUtils.jsonToObject(str,classType);
        }
        return value;
    }


}





/*
 * 这个方法用来比较object和map格式化的object中的数据
 * 参数object,map<String name,Object value>
 * 返回int
 * */
/*	public static int toCompare(Object o1,Map termMap) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		int fruit=0;
		Set<String> terms=termMap.keySet();
		for(String term:terms){
		Field field=o1.getClass().getField(term);
		Class type= field.getType();


		if(type==int.class ){
			fruit=field.getInt(o1)-Integer.valueOf( (String) termMap.get(term));
			if(fruit!=0){
				return fruit;
			}
		}
		if(type==String.class){
			 String s1=(String) field.get(o1);
			 String s2=(String) termMap.get(term);
			 fruit=s1.length()-s2.length();
			 if(fruit!=0){
				 return fruit;
			 }
			 fruit=((String)field.get(o1)).compareTo((String) termMap.get(term));
			if(fruit!=0){
				return fruit;
			}

		}

		}
		return fruit;
	}*/


      /*  public static boolean isCollection(Object object){
        Class type=object.getClass();
        if ("List".equals(type.getSimpleName()) || "Set".equals(type.getSimpleName())){
            return true;
        }
        return false;
    }*/


/*
 * 比较两个object对象中的数据是否相同
 * */
	/*public static int toCompare(Object o1,Object o2) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{

		if(!o1.getClass().equals(o2.getClass())){
			return -1;
		}
		int fruit=0;
		for(Field field:o1.getClass().getFields()){
		Class type= field.getType();

		if(type==int.class ){

			fruit=field.getInt(o1)-field.getInt(o2);
			if(fruit!=0){
				return fruit;
			}
		}
		if(type==String.class){
			 String s1=(String) field.get(o1);
			 String s2=(String) field.get(o2);
			 if(s1==null){
				 if(s2==null){
					 return 0;
				 }else{
					 return 1;
				 }
			 }
			 if(s2==null){
				 if(s1==null){
					 return 0;
				 }else{
					 return -1;
				 }
			 }

			 fruit=s1.length()-s2.length();
			 if(fruit!=0){
				 return fruit;
			 }
			 fruit=((String)field.get(o1)).compareTo((String)field.get(o2));
			if(fruit!=0){
				return fruit;
			}

		}

		}
		return fruit;
	}
*/


/*根据terms来比较object
 * 返回0时两个相等
 * terms是传入的比较规则
 * */
/*	public static int toCompare(Object o1,Object o2,String... terms) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		int fruit=0;
		for(String term:terms){
		Field field=o1.getClass().getField(term);
		Class type= field.getType();


		if(type==int.class ){
			fruit=field.getInt(o1)-field.getInt(o2);
			if(fruit!=0){
				return fruit;
			}
		}
		if(type==String.class){
			 String s1=(String) field.get(o1);
			 String s2=(String) field.get(o2);
			 fruit=s1.length()-s2.length();
			 if(fruit!=0){
				 return fruit;
			 }
			 fruit=((String)field.get(o1)).compareTo((String)field.get(o2));
			if(fruit!=0){
				return fruit;
			}

		}

		}
		return fruit;
	}*/
