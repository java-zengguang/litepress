package com.zg.util.reflect;

import com.zg.bean.entity.OptionDB;
import com.zg.database.util.DataBaseUtil;
import com.zg.init.Config;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/10 0010.
 */
public class FieldSQLUtils {
    public static String dateFormat = "yyyy-MM-dd";
    public static SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    private static OptionDB optionDB = (OptionDB) Config.getConfig("optionDB");


    private static String getFieldOrcale(Field field, Object object) throws IllegalArgumentException, IllegalAccessException {
        String value = null;
        String fieldType = null;
        Map<String, String> tableInfo =null;
        String tableName=FieldUtils.getTableNameFromModel(object.getClass());
        tableInfo = DataBaseUtil.getTableInfo(tableName);


        if(tableInfo!=null) {
            fieldType=tableInfo.get(field.getName().toLowerCase());
        }
        if(fieldType==null){
            fieldType=field.getType().getSimpleName().toLowerCase();
        }

        switch (fieldType) {
            case "number": {
                value = String.valueOf(field.get(object));
                break;
            }
            case "String": {
                if (field.get(object) == null || field.get(object).equals("")) {
                    value = "'null'";
                } else {
                    value = "'" + String.valueOf(field.get(object)) + "'";
                }
                break;
            }

            case "Date": {
                value = "to_date(" + sdf.format(field.get(object)) + ",'" + dateFormat + "')";
                break;
            }

        }
        if (value == null) {
            value = "";
        }
        return value;
    }


    private static String getFieldMySql(Field field, Object object) throws IllegalArgumentException, IllegalAccessException {
        String value = null;
        String fieldType = null;
        Map<String, String> tableInfo =null;
        String tableName=FieldUtils.getTableNameFromModel(object.getClass());
        tableInfo = DataBaseUtil.getTableInfo(tableName);

        if(tableInfo!=null) {
            fieldType=tableInfo.get(field.getName().toLowerCase());
        }
        if(fieldType==null){
            fieldType=field.getType().getSimpleName().toLowerCase();
        }

        switch (fieldType) {
            case "int": {
                value = String.valueOf(field.get(object));
                break;
            }
            case "tinyint":{
                if((Boolean) field.get(object)){
                  value="1";
                }else{
                    value="0";
                }
                break;
            }
            case "boolean":{
                if((Boolean) field.get(object)){
                    value="1";
                }else{
                    value="0";
                }
                break;
            }



            case "varchar": {
                if (field.get(object) == null || field.get(object).equals("")) {
                    value = "";
                } else {
                    value = "'" + String.valueOf(field.get(object)) + "'";
                }
                break;
            }

            case "string": {
                if (field.get(object) == null) {
                    value = "";
                } else {
                    value = "'" + String.valueOf(field.get(object)) + "'";
                }
                break;
            }

            case "date": {
                //  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                value = "'" + sdf.format(field.get(object)) + "'";
                break;
            }

            case "datetime": {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                value = "'" + sdf.format(field.get(object)) + "'";
                break;
            }

        }
        if (value == null) {
            value = "";
        }
        return value;
    }


    private static void setFieldMySql(Field field, Object object, String value) throws IllegalArgumentException, IllegalAccessException, ParseException {



        Map<String,String> tableInfo=null;

        String tableName=FieldUtils.getTableNameFromModel(object.getClass());
        tableInfo = DataBaseUtil.getTableInfo(tableName);
        String fieldType =null;
        if(tableInfo==null){
            fieldType=field.getType().getSimpleName().toLowerCase();
        } else {
            fieldType=tableInfo.get(field.getName().toLowerCase());
        }
        if (value == null || value.equals("") || value.equals("null") ) {

            if("String".equals(fieldType) || "varchar".equals(fieldType)){
                field.set(object,"");
            }

            return;
        }
        switch (fieldType) {
            case "int": {
                field.set(object, Integer.valueOf(value));
                break;
            }
            case "varchar": {
                field.set(object, value);
                break;
            }

            case "string":{
                field.set(object,value);
                break;
            }

            case "date": {
                //  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                field.set(object, sdf.parse(value));
                break;
            }

            case "datetime": {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                field.set(object, sdf.parse(value));
                break;
            }

            case "boolean":{
                if("true".endsWith(value)){
                    field.set(object,true);
                }
                if("false".endsWith(value)){
                    field.set(object,true);
                }

                break;
            }

            case "tinyint":{
                if("true".endsWith(value)){
                    field.set(object,true);
                }
                if("false".endsWith(value)){
                    field.set(object,true);
                }
                break;
            }
        }

    }


    public static String getFieldSql(Field field, Object object) throws IllegalAccessException {

        if("MYSQL".equals(optionDB.DBType)){
            return getFieldMySql(field,object);
        }else if("ORCALE".equals(optionDB.DBType)){
            return getFieldOrcale(field,object);
        }else{
            System.out.println(FieldUtils.class+"====数据源未初始化");
            return null;
        }
    }

    public static void setFieldSql(Field field, Object object,String value) throws IllegalAccessException, ParseException {

        if("MYSQL".equals(optionDB.DBType)){
             setFieldMySql(field,object,value);
             return;
        }else if("ORCALE".equals(optionDB.DBType)){
           // getFieldOrcale(field,object);
            return;
        }else{
            System.out.println(FieldUtils.class+"====数据源未初始化");
            return;
        }
    }
}
