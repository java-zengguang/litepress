package com.zg.database.util;

import com.zg.bean.entity.MainModel;
import com.zg.database.pool.DataBaseInte;
import com.zg.util.reflect.DynamicClass;
import com.zg.util.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class JDBCUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCUtils.class);
    private static DataBaseInte dataBasePool = DBPUtils.getInstance();
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal();

    private JDBCUtils() {
    }

    public static Connection getConnection() {
        Connection conn = threadLocal.get();
        if (conn == null) {
            conn = dataBasePool.getConnection();
            threadLocal.set(conn);
        }
        return conn;
    }

    public static int insertTable(Object model) throws SQLException, IllegalAccessException {
        List list=new ArrayList();
        list.add(model);
        int results[]=insertTables(list,model.getClass());
        int result=0;
        if(results!=null && results.length>0){
            result=results[0];
        }
        return result;
    }

    public static int[] insertTables(List modelLIst, Class modelClass) throws SQLException, IllegalAccessException {
       String tableName= FieldUtils.getTableNameFromModel(modelClass);
       return insertTables(modelLIst,modelClass,tableName);
    }

    //插入model_list ，未提交，未初始化连接
    public static int[] insertTables(List modelList, Class modelClass, String tableName) throws SQLException, IllegalAccessException {
        int[] result = null;
        String memS = "";
        String valS = "";
        Field[] modelFields = modelClass.getFields();
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        LOGGER.info("-----------------start batch-----------");
        for (Object model : modelList) {

            String sql=ModelSQLUtils.insert(model,tableName);
            LOGGER.info(sql);

            stmt.addBatch(sql);

        }
        LOGGER.info("------------------end batch-------------");
        result = stmt.executeBatch();
        stmt.close();
        return result;


    }

    //查询
    public static List select(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
        List list = new ArrayList();
        Map tableInfoMap = tableInfo(sql);
        list = selectToMapList(sql);
        Object model = DynamicClass.getDynamicClass(Arrays.asList("com.zg.bean.Model.MainModel"), getTableName(sql), tableInfoMap, null, "MainModel");
        list = SerializeObjectUtils.setMember(list, model);
        return list;
    }


    //查询
    public static List select(String sql, Class modelClass) throws Exception {
        List list = selectToMapList(sql);
        // Map<String, String> tableInfoMap = tableInfo(sql);
        List model_list = SerializeObjectUtils.setMember(list, modelClass);
        return model_list;

    }

    public static String getTableName(String sql) {
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
    public static Map<String, String> tableInfo(String sql) throws SQLException {
        Map map = new HashMap();
        Connection conn = getConnection();
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
    public static List<Map> selectToMapList(String sql) throws SQLException {

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
                map.put(rsmd.getColumnLabel(i), rs.getObject(i) + "");
            }

            list.add(map);
        }
        pstmt.close();
        rs.close();

        return list;
    }

    //执行增删改
    public static Integer operation(String sql) throws SQLException {
        LOGGER.info(sql);
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int x = pstmt.executeUpdate();
        pstmt.close();
        return x;
    }


    //执行批操作
    public static int[] batchSql(List<String> sqlList) {
        int i[] = null;
        Statement stmt;
        Connection conn;

        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return i;
    }




    public static List execute(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
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


    public static boolean commit() {
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


    public static void release() {
        Connection conn = getConnection();
        dataBasePool.release(conn);
        threadLocal.remove();
    }

    public static int updateModel(Object object,String... terms) throws SQLException, IllegalAccessException {
        int result=0;
        if (terms != null && terms.length > 0) {
            String  sql = ModelSQLUtils.update(object, terms);
            result=JDBCUtils.operation(sql);
        }
        return result;
    }





    /*    public static void rollBack(){
        Connection conn=getConnection();
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/


}
