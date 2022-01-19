package com.zg.util.sinosing;

import com.zg.bean.entity.MainModel;
import com.zg.bean.entity.OptionDB;
import com.zg.database.pool.DataBaseInte;
import com.zg.database.util.DBPUtils;
import com.zg.database.util.ModelSQLUtils;
import com.zg.database.util.NewDBPUtils;
import com.zg.database.util.SerializeObjectUtils;
import com.zg.util.reflect.DynamicClass;
import com.zg.util.reflect.FieldUtils;
import org.apache.poi.hssf.record.pivottable.StreamIDRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.util.*;


public class NewJDBCUtil {
    private final Logger LOGGER = LoggerFactory.getLogger(NewJDBCUtil.class);
    private String dataSource;
    private DataBaseInte dataBasePool;
    private Connection conn;
    private ThreadLocal<Connection> threadLocal = new ThreadLocal();

    public NewJDBCUtil(String dataSource) {
        this.dataSource = dataSource;
        init();
    }


    private boolean init() {
        if (dataBasePool == null) {
            dataBasePool = NewDBPUtils.getInstance(dataSource);
        }
        if (conn == null) {
            conn = getConnection();
        }
        return true;
    }

    public Connection getConnection() {
        Connection conn = threadLocal.get();
        if (conn == null) {
            conn = dataBasePool.getConnection();
            threadLocal.set(conn);
        }
        return conn;
    }

    public int insertTable(Object model) throws SQLException, IllegalAccessException {
        List list = new ArrayList();
        list.add(model);
        int results[] = insertTables(list, model.getClass());
        int result = 0;
        if (results != null && results.length > 0) {
            result = results[0];
        }
        return result;
    }

    public int[] insertTables(List modelLIst, Class modelClass) throws SQLException, IllegalAccessException {
        String tableName = FieldUtils.getTableNameFromModel(modelClass);
        return insertTables(modelLIst, modelClass, tableName);
    }

    //插入model_list ，未提交，未初始化连接
    public int[] insertTables(List modelList, Class modelClass, String tableName) throws SQLException, IllegalAccessException {
        int[] result = null;
        String memS = "";
        String valS = "";
        Field[] modelFields = modelClass.getFields();
        Statement stmt = conn.createStatement();
        LOGGER.info("-----------------start batch-----------");
        for (Object model : modelList) {

            String sql = ModelSQLUtils.insert(model, tableName);
            LOGGER.info(sql);

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
        Object model = DynamicClass.getDynamicClass(Arrays.asList("com.zg.bean.Model.MainModel"), getTableName(sql), tableInfoMap, null, "MainModel");
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
        LOGGER.info("JDBCUtils.getTableName   未找到tableName");
        return null;
    }

    //获取表格信息
    public Map<String, String> tableInfo(String sql) throws SQLException {
        Map map = new HashMap();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs;
        rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;
        columncount = rsmd.getColumnCount();
        for (int i = 1; i < columncount + 1; i++) {
            map.put(FieldUtils.dataTranslateJava(rsmd.getColumnLabel(i)), FieldUtils.dataTranslateJava(rsmd.getColumnTypeName(i)));
        }
        rs.close();
        pstmt.close();
        //dataBasePool.release(conn);
        return map;
    }

    //查询出列明，数据对应的list集合
    public List<Map> selectToMapList(String sql) throws SQLException {

        // 记录error级别的信息
        LOGGER.info(sql);
        List list = new ArrayList();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;

        while (rs.next()) {
            Map map = new LinkedHashMap();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                map.put(rsmd.getColumnLabel(i), rs.getObject(i) + "");
            }

            list.add(map);
        }
        pstmt.close();
        rs.close();

        return list;
    }

    //转义特殊字符
    private String formatSQL(String sql){
        sql=sql.replaceAll("'","\'");
        sql=sql.replaceAll("\"","\"");
        return sql;
    }

    //执行增删改
    public Integer operation(String sql) throws SQLException {
        LOGGER.info(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int x = pstmt.executeUpdate();
        pstmt.close();
        return x;
    }


    //执行批操作
    private int[] batchSql(List<String> sqlList) throws SQLException {

        int result[] = new int[sqlList.size()];
        for (int i = 0; i < sqlList.size(); i++) {
            String sql = sqlList.get(i);
            result[i] = operation(sql);
        }

        return result;
    }

    //执行批操作
    public int[] batchSql(List<String> sqlList, Boolean model) throws SQLException {
        if (!model) {
            return batchSql(sqlList);
        } else {
            return batchOneSql(sqlList);
        }

    }

    //执行批操作
    private int[] batchOneSql(List<String> sqlList) throws SQLException {
        //清洗脚本
        for (String sql : sqlList) {
            sql = sql.replace(";", "");
        }

        int i[] = null;
        Statement stmt;
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

    public List execute(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
        LOGGER.info(sql);
        List<MainModel> list = new ArrayList<MainModel>();
        if (sql == null) {
            LOGGER.info("JDBCUtils.execute  sql is null");
        } else if (sql.startsWith("select")) {
            list = select(sql);

        } else if (operation(sql) > 0) {


        }
        return list;
    }

    public void release() {
        dataBasePool.release(conn);
        threadLocal.remove();
    }

    public boolean commit() {

        dataBasePool.commit(conn);
        release();
        return true;
    }


    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException {
        int result = 0;
        if (terms != null && terms.length > 0) {
            String sql = ModelSQLUtils.update(object, terms);
            result = operation(sql);
        }
        return result;
    }


    public void colseConnect() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public List<String> selectOneColList(String sql) throws SQLException {
        List list = new ArrayList();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs;
        rs = pstmt.executeQuery();
        while (rs.next()) {
            String value = rs.getString(1); // 此方法比较高效
            list.add(value);
        }
        rs.close();
        pstmt.close();
        conn.close();
        //dataBasePool.release(conn);
        return list;
    }
}
