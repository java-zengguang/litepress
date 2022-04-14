package com.zg.database.util;

import com.zg.bean.entity.MainModel;
import com.zg.util.reflect.DynamicClass;
import com.zg.util.reflect.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class BaseDao {
    private final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);
    public String dataSource = "optionDB";


    public Connection getConnection() throws SQLException, ClassNotFoundException {
        return NewDBPUtils.getConnection(dataSource);
    }

    public int insertTable(Object model) throws SQLException, IllegalAccessException, ClassNotFoundException {
        List list = new ArrayList();
        list.add(model);
        int results[] = insertTables(list, model.getClass());
        int result = 0;
        if (results != null && results.length > 0) {
            result = results[0];
        }
        return result;
    }

    public int[] insertTables(List modelLIst, Class modelClass) throws SQLException, IllegalAccessException, ClassNotFoundException {
        String tableName = EntityUtils.getTableNameFromModel(modelClass);
        return insertTables(modelLIst, modelClass, tableName);
    }

    //插入model_list ，未提交，未初始化连接
    public int[] insertTables(List modelList, Class modelClass, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException {
        int[] result = null;
        String memS = "";
        String valS = "";
        Field[] modelFields = modelClass.getFields();
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        LOGGER.info("-----------------start batch-----------");
        for (Object model : modelList) {

            String sql = ModelSQLUtils.insert(model, tableName);
            // LOGGER.info(sql);
            stmt.addBatch(sql);

        }
        LOGGER.info("------------------end batch-------------");
        result = stmt.executeBatch();
        stmt.close();
        return result;


    }

    //查询
    public List select(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
        List list = new ArrayList();
        Map tableInfoMap = tableInfo(sql);
        list = selectToMapList(sql);
        Object model = DynamicClass.getDynamicClass(Arrays.asList("com.zg.bean.entity.MainModel", "com.zg.bean.annotation.FieldTypeMode", "com.zg.bean.annotation.Model"), getTableName(sql), tableInfoMap, null, "MainModel");
        list = SerializeObjectUtils.setMember(list, model.getClass());
        return list;
    }


    //查询
    public List select(String sql, Class modelClass) throws Exception {
        List list = selectToMapList(sql);
        // Map<String, String> tableInfoMap = tableInfo(sql);
        List model_list = SerializeObjectUtils.setMember(list, modelClass);
        return model_list;

    }

    public String getTableName(String sql) {
        String stringArray[] = sql.split(" ");
        for (int i = 0; i < stringArray.length; i++) {
            if ("from".equals(stringArray[i].toLowerCase().trim()) || "*from".equals(stringArray[i].toLowerCase().trim())) {
                return stringArray[i + 1];
            }
        }
        LOGGER.info(" getTableName   未找到tableName");
        return null;
    }

    //获取表格信息
    public Map<String, String> tableInfo(String sql) throws SQLException, ClassNotFoundException {
        Map map = new HashMap();
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs;
        rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;
        columncount = rsmd.getColumnCount();
        for (int i = 1; i < columncount + 1; i++) {
            map.put(EntityUtils.dataTranslateJava(rsmd.getColumnLabel(i)), EntityUtils.dataTranslateJava(rsmd.getColumnTypeName(i)));
        }
        rs.close();
        pstmt.close();
        //dataBasePool.release(conn);
        return map;
    }

    //查询出列明，数据对应的list集合
    public List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {

        // 记录error级别的信息
        LOGGER.info(sql);
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

    //执行增删改
    public Integer operation(String sql) throws SQLException, ClassNotFoundException {
        LOGGER.info(sql);
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int x = pstmt.executeUpdate();
        pstmt.close();
        return x;
    }


    //执行批操作
    public int[] batchSql(List<String> sqlList) throws SQLException, ClassNotFoundException {
        int i[] = null;
        Statement stmt;
        Connection conn;

        conn = getConnection();
        stmt = conn.createStatement();
        LOGGER.info("--------------start batch-----------");
        for (String sql : sqlList) {
            LOGGER.info(sql);
            stmt.addBatch(sql);
        }
        i = stmt.executeBatch();
        LOGGER.info("--------------end batch-----------");
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
                LOGGER.info(lineStr);
                // 判断截取点
                if (lineStr.endsWith(";")) {
                    lineStr = lineStr.replace(";", "");
                    list.add(new String(lineStr));
                    lineStr = "";
                }
            }

        } else {
            LOGGER.info("Sql文件没找到！");
        }
        return list;
    }


    public List execute(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
        LOGGER.info(sql);
        List<MainModel> list = new ArrayList<MainModel>();
        if (sql == null) {
            LOGGER.info(" execute  sql is null");
        } else if (sql.startsWith("select")) {
            list = select(sql);

        } else if (operation(sql) > 0) {


        }
        return list;
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

    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException, ClassNotFoundException {
        int result = 0;
        if (terms != null && terms.length > 0) {
            String sql = ModelSQLUtils.update(object, terms);
            result = operation(sql);
        }
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


    public Integer insertMap2Data(String tableName, Map<String, String> para) throws Exception {
        String sql = "insert into " + tableName;
        String column = "";
        String values = "";
        Set<String> columnSet = para.keySet();
        for (String c : columnSet) {
            column = c + " ," + column;
            values = para.get(c) + " ," + "'" + values + "'";
        }
        if (column.endsWith(",")) {
            column = column.substring(0, column.length() - 1);
        }
        if (values.endsWith(",")) {
            values = values.substring(0, values.length() - 1);
        }
        sql = sql + " (" + column + ")" + " values (" + values + ")";
        return operation(sql);

    }

}
