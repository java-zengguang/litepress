package com.zg.common.dao.database;


import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.bean.entity.OptionDB;
import com.zg.common.dao.template.EntityDaoTemplate;
import com.zg.common.dao.template.EntityDaoTemplateFactory;
import com.zg.common.init.Config;
import com.zg.common.service.BaseService;
import com.zg.common.util.database.ParseSQLUtils;
import com.zg.common.util.reflect.ModelSQLUtils;
import net.sf.jsqlparser.JSQLParserException;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class BaseJDBCDao extends BaseService {
    public String dataSource = "optionDB";


    public Connection getConnection() throws SQLException, ClassNotFoundException {
        return getConnection(dataSource);
    }

    //查询出列明，数据对应的list集合
    public List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {

        // 记录error级别的信息
        Logger.debug(sql);
        List list = new ArrayList();
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;

        while (rs.next()) {
            Map map = new LinkedHashMap();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                Object columnValue = rs.getObject(i);
                map.put(columnLabel, columnValue);
            }

            list.add(map);
        }
        pstmt.close();
        rs.close();

        return list;
    }

    //查询出列明，数据对应的list集合
    public List<List<MetadataEntity>> select2TempleList(String sql, String tableName) throws SQLException, ClassNotFoundException {
        Logger.debug(sql);
        tableName = tableName.trim().toUpperCase();
        String ownName = "";
        if (tableName.contains(".")) {
            String[] splits = tableName.split("\\.");
            ownName = splits[0];
            tableName = splits[1];
        }
        //获取链接
        Connection conn = NewDBPUtils.getConnection(dataSource);

        //获取组件
        List<String> pkColumnList = new ArrayList<>();
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName);
        while (dmdrs.next()) {
            String pkStr = dmdrs.getString("COLUMN_NAME");
            pkColumnList.add(pkStr);
        }
        //获取数据
        List list = new ArrayList();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(optionDB.DBType);

        int columncount = 0;
        while (rs.next()) {
            List<MetadataEntity> columnList = new ArrayList<>();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                String columnType = rsmd.getColumnTypeName(i);
                Integer columnScale = rsmd.getScale(i);
                if (columnScale == -127) {
                    columnScale = 6;
                }
                Object columnValue = rs.getObject(i);
                MetadataEntity metadataEntity = new MetadataEntity();
                metadataEntity.ownName = ownName;
                metadataEntity.tableName = tableName;
                metadataEntity.columnLabel = columnLabel;
                metadataEntity.columnType = columnType;
                metadataEntity.columnScale = columnScale;
                metadataEntity.objectValue = columnValue;
                metadataEntity.dbType = optionDB.DBType;
                if (pkColumnList.contains(columnLabel)) {
                    metadataEntity.isPK = "1";
                } else {
                    metadataEntity.isPK = "0";
                }
                if (rsmd.isAutoIncrement(i)) {
                    metadataEntity.isAutoIncrease = "1";  //自增
                    //  metadataEntity.isNotCommit="1"; //自增不提交
                } else {
                    metadataEntity.isAutoIncrease = "0";
                    metadataEntity.isNotCommit = "0";  //不自增的列才提交
                }
                metadataEntity = entityDaoTemplate.translateEntity(metadataEntity);
                columnList.add(metadataEntity);
            }
            list.add(columnList);
        }
        pstmt.close();
        rs.close();

        return list;
    }

    //查询出列明，数据对应的list集合
    public List<List<MetadataEntity>> select2TempleList(String sql) throws SQLException, ClassNotFoundException, JSQLParserException {
        Logger.debug(sql);
        //获取表名
        List<String> tableNameList = ParseSQLUtils.parseSelectMainTable(sql);
        //获取链接
        Connection conn = getConnection();
        //获取组件
        Set<String> pkColumnList = new HashSet<>();
        DatabaseMetaData dmd = conn.getMetaData();
        for (String tableName : tableNameList) {
            ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName);
            while (dmdrs.next()) {
                String pkStr = dmdrs.getString("COLUMN_NAME");
                pkColumnList.add(pkStr);
            }
        }
        //合并主表
        StringBuffer tableNameBuffer = new StringBuffer();
        tableNameList.forEach(tableNameBuffer::append);
        //获取数据
        List list = new ArrayList();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(optionDB.DBType);
        int columncount = 0;
        while (rs.next()) {
            List<MetadataEntity> columnList = new ArrayList<>();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                String columnType = rsmd.getColumnTypeName(i);
                Integer columnScale = rsmd.getScale(i);
                if (columnScale == -127) {
                    columnScale = 6;
                }
                String columnName = rsmd.getColumnName(i);
                Object columnValue = rs.getObject(i);
                MetadataEntity metadataEntity = new MetadataEntity();
                metadataEntity.ownName = "";
                metadataEntity.tableName = tableNameBuffer.toString();
                metadataEntity.columnLabel = columnLabel;
                metadataEntity.columnType = columnType;
                metadataEntity.columnScale = columnScale;
                metadataEntity.objectValue = columnValue;
                metadataEntity.dbType = optionDB.DBType;
                if (pkColumnList.contains(columnName)) {
                    metadataEntity.isPK = "1";
                } else {
                    metadataEntity.isPK = "0";
                }
                if (rsmd.isAutoIncrement(i)) {
                    metadataEntity.isAutoIncrease = "1";  //自增
                    metadataEntity.isNotCommit = "1"; //自增不提交
                } else {
                    metadataEntity.isAutoIncrease = "0";
                    metadataEntity.isNotCommit = "0";  //不自增的列才提交
                }
                metadataEntity = entityDaoTemplate.translateEntity(metadataEntity);
                columnList.add(metadataEntity);
            }
            list.add(columnList);
        }
        pstmt.close();
        rs.close();

        return list;
    }

    //执行批操作
    private int[] batchSql(List<String> sqlList) throws SQLException, ClassNotFoundException {

        int result[] = new int[sqlList.size()];
        for (int i = 0; i < sqlList.size(); i++) {
            String sql = sqlList.get(i);
            result[i] = operation(sql);
        }

        return result;
    }

    //执行批操作
    public int[] batchSql(List<String> sqlList, Boolean model) throws SQLException, ClassNotFoundException {
        if (!model) {
            return batchSql(sqlList);
        } else {
            return batchOneSql(sqlList);
        }

    }

    //执行批操作
    private int[] batchOneSql(List<String> sqlList) throws SQLException, ClassNotFoundException {
        //清洗脚本
        for (String sql : sqlList) {
            sql = sql.replace(";", "");
        }

        int i[] = null;
        Statement stmt;
        Connection conn = NewDBPUtils.getConnection(dataSource);

        stmt = conn.createStatement();
        Logger.debug("--------------start batch-----------");
        for (String sql : sqlList) {
            Logger.debug(sql);
            stmt.addBatch(sql);
        }
        i = stmt.executeBatch();
        Logger.debug("--------------end batch-----------");
        stmt.close();

        return i;
    }


    public List<String> batchSqlFile(File file) throws IOException {
        // 装载list
        List<String> list = new ArrayList<String>();
        if (file != null && file.exists()) {
            // 读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String lineStr = "";
            while ((line = br.readLine()) != null) {
                lineStr = lineStr + line;
                Logger.debug(lineStr);
                // 判断截取点
                if (lineStr.endsWith(";")) {
                    lineStr = lineStr.replace(";", "");
                    list.add(new String(lineStr));
                    lineStr = "";
                }
            }

        } else {
            Logger.debug("Sql文件没找到！");
        }
        return list;
    }

    //插入model_list ，未提交，未初始化连接
    public int[] insertTables(List modelList, Class modelClass, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int[] result = null;

        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        Logger.debug("-----------------start batch-----------");
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        for (Object model : modelList) {

            String sql = ModelSQLUtils.insert(model, tableName, optionDB.DBType);
            stmt.addBatch(sql);

        }
        Logger.debug("------------------end batch-------------");
        result = stmt.executeBatch();
        stmt.close();
        return result;
    }

    public String getOneValue(String sql) throws SQLException, ClassNotFoundException {
        String result = "";
        ResultSet rs = null;
        PreparedStatement pstmt = null;


        Connection conn = getConnection();
        pstmt = conn.prepareStatement(sql);

        rs = pstmt.executeQuery();
        while (rs.next()) {
            result = rs.getString(1);
        }


        return result;
    }

    //执行增删改
    public Integer operation(String sql) throws SQLException, ClassNotFoundException {
        Logger.debug(sql);
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int x = pstmt.executeUpdate();
        pstmt.close();
        return x;
    }


}
