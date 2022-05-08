package com.zg.common.dao.database;


import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.dao.mongodb.ModelSQLUtils;
import com.zg.common.dao.template.EntityDaoTemplate;
import com.zg.common.dao.template.SimpleEntityDaoTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class BaseJDBCDao {
    public  final Logger logger = LoggerFactory.getLogger(this.getClass());
    public String dataSource = "optionDB";


    public Connection getConnection() throws SQLException, ClassNotFoundException {
        return NewDBPUtils.getConnection(dataSource);
    }
    //查询出列明，数据对应的list集合
    public List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {

        // 记录error级别的信息
        logger.debug(sql);
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
    private String getTableName(String sql) {
        String tableName="";
        String stringArray[] = sql.split(" ");
        for (int i = 0; i < stringArray.length; i++) {
            if ("from".equals(stringArray[i].toLowerCase().trim()) || "*from".equals(stringArray[i].toLowerCase().trim())) {
                tableName= stringArray[i + 1];
                if(tableName.contains(",")){
                    tableName.replace(",","And");
                }
            }
        }
        return tableName;
    }

    //查询出列明，数据对应的list集合
    public List<List<MetadataEntity>> select2TempleList(String sql) throws SQLException, ClassNotFoundException {
        logger.debug(sql);
        String tableName="";
        tableName=getTableName(sql);
        List list = new ArrayList();
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;
        while (rs.next()) {
            List<MetadataEntity> columnList=new ArrayList<>();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                String columnType=rsmd.getColumnTypeName(i);
                Object columnValue = rs.getObject(i);
                MetadataEntity metadataEntity =new MetadataEntity();
                metadataEntity.tableName=tableName;
                metadataEntity.columnLabel=columnLabel;
                metadataEntity.columnType=columnType;
                metadataEntity.objectValue =columnValue;
                EntityDaoTemplate entityDaoTemplate=new SimpleEntityDaoTemplate();
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
        logger.debug("--------------start batch-----------");
        for (String sql : sqlList) {
            logger.debug(sql);
            stmt.addBatch(sql);
        }
        i = stmt.executeBatch();
        logger.debug("--------------end batch-----------");
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
                logger.debug(lineStr);
                // 判断截取点
                if (lineStr.endsWith(";")) {
                    lineStr = lineStr.replace(";", "");
                    list.add(new String(lineStr));
                    lineStr = "";
                }
            }

        } else {
            logger.debug("Sql文件没找到！");
        }
        return list;
    }

    //插入model_list ，未提交，未初始化连接
    public int[] insertTables(List modelList, Class modelClass, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int[] result = null;

        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        logger.debug("-----------------start batch-----------");
        for (Object model : modelList) {

            String sql = ModelSQLUtils.insert(model, tableName);
            stmt.addBatch(sql);

        }
        logger.debug("------------------end batch-------------");
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
        logger.debug(sql);
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int x = pstmt.executeUpdate();
        pstmt.close();
        return x;
    }

    public boolean commit() throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        try {
            if (!conn.getAutoCommit()) {
                conn.commit();
                release();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }



    public void release() throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        conn.close();
        NewDBPUtils.release(dataSource);
    }

}
