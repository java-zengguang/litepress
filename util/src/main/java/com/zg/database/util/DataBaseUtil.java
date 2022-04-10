package com.zg.database.util;

import com.zg.util.reflect.ListUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class DataBaseUtil {
    private static Map<String, Map> tableInfoMap = new HashMap<>();


    private static Map loadTableInfo(String tableName) {

        Map tableInfo=tableInfoMap.get(tableName);
        if(tableInfo==null) {

            String tableInfoSQL = "SELECT COLUMN_NAME,DATA_TYPE FROM " + "information_schema.columns" +
                    " WHERE  table_name='" + tableName + "' AND table_schema=(SELECT DATABASE())";
            List list = null;
            try {
                NewJDBCUtil jdbcUtil=new NewJDBCUtil("opthinDB");
                list =  jdbcUtil.selectToMapList(tableInfoSQL);
                jdbcUtil.release();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
             tableInfo = ListUtils.createMap(list, "COLUMN_NAME", "DATA_TYPE");
            if (tableInfo != null && !tableInfo.isEmpty()) {
                tableInfoMap.put(tableName, tableInfo);
            }
        }

        return tableInfo;
    }


    public static Map getTableInfo(String tableName)  {
        Map map=null;
        if(tableName!=null) {
            map = loadTableInfo(tableName);
        }
        return map;

    }

    private static void loadTableListInfo() throws SQLException, ClassNotFoundException {
        String tableName = "";

        String tableListSQL = "SELECT TABLE_NAME FROM information_schema.tables" +
                " WHERE table_schema=(SELECT DATABASE())";
        NewJDBCUtil jdbcUtil=new NewJDBCUtil("opthinDB");
        List<Map> tableNameList =  jdbcUtil.selectToMapList(tableListSQL);


        for (Map tableNameMap : tableNameList) {
            tableName = tableNameMap.get("TABLE_NAME").toString();
            String tableInfoSQL = "SELECT COLUMN_NAME,DATA_TYPE FROM " + "information_schema.columns" +
                    " WHERE  table_name='" + tableName + "' AND table_schema=(SELECT DATABASE())";
            List list =  jdbcUtil.selectToMapList(tableInfoSQL);
            jdbcUtil.release();
            Map tableInfo = ListUtils.createMap(list, "COLUMN_NAME", "DATA_TYPE");
            tableInfoMap.put(tableName, tableInfo);
        }

        System.out.println(DataBaseUtil.class + "====tableInfoMap:" + tableInfoMap);
    }


    private static Map<String, Map> getTableListInfo() throws SQLException, ClassNotFoundException {
        if (tableInfoMap == null || tableInfoMap.size() == 0) {
            loadTableListInfo();
            getTableListInfo();
        }
        return tableInfoMap;
    }

}
