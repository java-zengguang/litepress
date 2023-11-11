package com.zg.common.dao.database;

import com.zg.common.bean.entity.ColumnInfo;
import com.zg.common.bean.entity.OptionDB;
import com.zg.common.bean.entity.TableInfo;
import com.zg.common.init.Config;
import com.zg.common.util.reflect.ListUtils;
import org.tinylog.Logger;

import java.sql.*;
import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class DataBaseUtil {

    private static Map<String, Map> tableInfoMap = new HashMap<>();


    private static Map loadTableInfo(String tableName) {

        Map tableInfo = tableInfoMap.get(tableName);
        if (tableInfo == null) {

            String tableInfoSQL = "SELECT COLUMN_NAME,DATA_TYPE FROM " + "information_schema.columns" +
                    " WHERE  table_name='" + tableName + "' AND table_schema=(SELECT DATABASE())";
            List list = null;
            try {
                NewJDBCUtil jdbcUtil = new NewJDBCUtil("opthinDB");
                list = jdbcUtil.selectToMapList(tableInfoSQL);
            } catch (SQLException | ClassNotFoundException e) {
                Logger.error(e);
            }
            tableInfo = ListUtils.createMap(list, "COLUMN_NAME", "DATA_TYPE");
            if (tableInfo != null && !tableInfo.isEmpty()) {
                tableInfoMap.put(tableName, tableInfo);
            }
        }

        return tableInfo;
    }


    public static Map getTableInfo(String tableName) {
        Map map = null;
        if (tableName != null) {
            map = loadTableInfo(tableName);
        }
        return map;

    }

    private static void loadTableListInfo() throws SQLException, ClassNotFoundException {
        String tableName = "";

        String tableListSQL = "SELECT TABLE_NAME FROM information_schema.tables" +
                " WHERE table_schema=(SELECT DATABASE())";
        NewJDBCUtil jdbcUtil = new NewJDBCUtil("opthinDB");
        List<Map> tableNameList = jdbcUtil.selectToMapList(tableListSQL);


        for (Map tableNameMap : tableNameList) {
            tableName = tableNameMap.get("TABLE_NAME").toString();
            String tableInfoSQL = "SELECT COLUMN_NAME,DATA_TYPE FROM " + "information_schema.columns" +
                    " WHERE  table_name='" + tableName + "' AND table_schema=(SELECT DATABASE())";
            List list = jdbcUtil.selectToMapList(tableInfoSQL);
            Map tableInfo = ListUtils.createMap(list, "COLUMN_NAME", "DATA_TYPE");
            tableInfoMap.put(tableName, tableInfo);
        }

        Logger.info(DataBaseUtil.class + "====tableInfoMap:" + tableInfoMap);
    }


    private static Map<String, Map> getTableListInfo() throws SQLException, ClassNotFoundException {
        if (tableInfoMap == null || tableInfoMap.size() == 0) {
            loadTableListInfo();
            getTableListInfo();
        }
        return tableInfoMap;
    }


    //查询数据库中的表名
    public static List<String> getTableNameList(String dataSource) throws SQLException, ClassNotFoundException {
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        Connection conn = NewDBPUtils.getConnection(dataSource);
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(optionDB.databaseName, optionDB.username.toUpperCase(), "%", new String[]{"TABLE"});
        List<String> tableNameList = new ArrayList<>();
        while (rs.next()) {
            tableNameList.add(rs.getString("TABLE_NAME"));
        }
        NewDBPUtils.release(dataSource);
        return tableNameList;
    }


    //查询数据库中的表结构
    public static List<TableInfo> getTableInfoList(String dataSource, String tableName) throws SQLException, ClassNotFoundException {
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        Connection conn = NewDBPUtils.getConnection(dataSource);
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(optionDB.databaseName, optionDB.username.toUpperCase(), "%" + tableName + "%", new String[]{"TABLE"});
        List<TableInfo> tableInfoList = new ArrayList<>();
        while (rs.next()) {
            TableInfo tableInfo = new TableInfo();
            tableInfo.tableName = rs.getString("TABLE_NAME");
            ResultSetMetaData rsMD = rs.getMetaData();
            List<ColumnInfo> columnInfoList = new ArrayList<>();
            Integer columnCount = rsMD.getColumnCount();
            for (int i = 1; i <= columnCount; i++) { // 获取列名称
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.columnName = rsMD.getColumnName(i);
                columnInfoList.add(columnInfo);
            }
            tableInfo.columnList = columnInfoList;
            tableInfoList.add(tableInfo);
        }
        NewDBPUtils.release(dataSource);
        return tableInfoList;
    }


    public static TableInfo getTableInfo(String dataSource, String tableName) throws SQLException, ClassNotFoundException {
        Connection conn = NewDBPUtils.getConnection(dataSource);
        DatabaseMetaData dbmd = conn.getMetaData();

        TableInfo tableInfo = new TableInfo();
        tableInfo.tableName = tableName;

        //获取组件
        Set<String> pkSet=new HashSet<>();
        List<ColumnInfo> pkColumnList = new ArrayList<>();
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName.toUpperCase());
        while (dmdrs.next()) {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.columnName=dmdrs.getString("COLUMN_NAME");
            pkColumnList.add(columnInfo);
            pkSet.add(columnInfo.columnName);
        }
        ResultSet rs = dbmd.getColumns(null, null, tableName.toUpperCase(), "%");
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        while (rs.next()) {
            ColumnInfo columnInfo = new ColumnInfo();
            columnInfo.columnName = rs.getString("COLUMN_NAME");
            columnInfo.columnType = rs.getString("TYPE_NAME");
            columnInfo.isNullAble =  rs.getString("IS_NULLABLE");
            columnInfo.columnSize=  rs.getString("COLUMN_SIZE");
            columnInfo.decimalDigits= rs.getString("DECIMAL_DIGITS");
            if(pkSet.contains(columnInfo.columnName)){
                columnInfo.isPK="1";
            }else{
                columnInfo.isPK="0";
            }
            String columnLine= columnInfo.columnName+"  "+columnInfo.columnType;

            if(!Arrays.asList("DATE","ENUM","TIME","DATETIME","BOOL","BOOLEAN","TEXT","BLOB").contains(columnInfo.columnType)&&!("NUMBER".equals(columnInfo.columnType) &&"-127".equals(columnInfo.decimalDigits) && "0".equals(columnInfo.columnSize))) {
                columnLine = columnLine + "(" + columnInfo.columnSize;
                if (columnInfo.decimalDigits != null) {
                    columnLine = columnLine + "," + columnInfo.decimalDigits;
                }
                columnLine = columnLine + ") ";
            }
            if("NO".equals(columnInfo.isNullAble)){
                columnLine=columnLine+" not null ";
            }
            columnInfo.columnLine=columnLine;
            columnInfoList.add(columnInfo);
        }
        tableInfo.columnList = columnInfoList;
        tableInfo.pkColumnList=pkColumnList;
        NewDBPUtils.release(dataSource);
        return tableInfo;
    }


}
