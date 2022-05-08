package com.zg.common.dao.database;

import com.zg.common.bean.entity.MainModel;
import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.dao.assemble.SimpleAssemble;
import com.zg.common.dao.mongodb.ModelSQLUtils;
import com.zg.common.dao.template.EntityDaoTemplate;
import com.zg.common.dao.template.SimpleEntityDaoTemplate;
import com.zg.common.util.reflect.SerializeObjectUtils;
import com.zg.common.util.reflect.DynamicClass;
import com.zg.common.util.reflect.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.util.*;


public class NewJDBCUtil  {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private String dataSource;


    public NewJDBCUtil(String dataSource) {
        this.dataSource = dataSource;
    }

    public int insertTable(Object model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        List list = new ArrayList();
        list.add(model);
        int results[] = insertTables(list, model.getClass());
        int result = 0;
        if (results != null && results.length > 0) {
            result = results[0];
        }
        return result;
    }

    public int[] insertTables(List modelLIst, Class modelClass) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        String tableName = EntityUtils.getTableNameFromModel(modelClass);
        return insertTables(modelLIst, modelClass, tableName);
    }

    //插入model_list ，未提交，未初始化连接
    public int[] insertTables(List modelList, Class modelClass, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int[] result = null;
        Connection conn = NewDBPUtils.getConnection(dataSource);
        Statement stmt = conn.createStatement();
        logger.debug("-----------------start batch-----------");
        for (Object model : modelList) {
            String sql = ModelSQLUtils.insert(model, tableName);
            logger.debug(sql);

            stmt.addBatch(sql);

        }
        logger.debug("------------------end batch-------------");
        result = stmt.executeBatch();
        stmt.close();
        return result;


    }

/*
    //查询
    public List select(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
        List list = new ArrayList();
        Map tableInfoMap = tableInfo(sql);
        list = selectToMapList(sql);
        if(list!=null && list.size()>0) {
            Class model = DynamicClass.getDynamicModel(Arrays.asList("com.zg.common.bean.entity.MainModel", "com.zg.common.annotation.FieldTypeMode", "com.zg.common.annotation.Model", "java.math.BigDecimal"), getTableName(sql), tableInfoMap, null, "MainModel");
            list = SerializeObjectUtils.setMember(list, model);
        }
        return list;
    }
*/

    //查询
    public List select(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
        List<List<MetadataEntity>> templeList = select2TempleList(sql);
        SimpleAssemble simpleAssemble=new SimpleAssemble();
        List modelList=new ArrayList();
        Class modelClass = null;
        if(templeList!=null&&templeList.size()>0) {
            modelClass=DynamicClass.getDynamicModel(templeList.get(0));
            for (List<MetadataEntity> columnList : templeList) {
                Object obj = modelClass.newInstance();
                for (MetadataEntity metadataEntity : columnList) {
                    obj = simpleAssemble.assembling(metadataEntity, obj);
                }
                modelList.add(obj);
            }
        }
        return modelList;
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
        logger.debug(" getTableName   未找到tableName");
        return null;
    }

    //获取表格信息
    public Map<String, String> tableInfo(String sql) throws SQLException, ClassNotFoundException {
        Map map = new HashMap();
        Connection conn = NewDBPUtils.getConnection(dataSource);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs;
        rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;
        columncount = rsmd.getColumnCount();
        for (int i = 1; i < columncount + 1; i++) {
            map.put(rsmd.getColumnLabel(i), EntityUtils.dataTranslateJava(rsmd.getColumnTypeName(i)));
        }
        rs.close();
        pstmt.close();
        //dataBasePool.release(conn);
        return map;
    }

    //查询出列明，数据对应的list集合
    public List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {

        // 记录error级别的信息
        logger.debug(sql);
        List list = new ArrayList();
        Connection conn = NewDBPUtils.getConnection(dataSource);
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

    //转义特殊字符
    private String formatSQL(String sql) {
        sql = sql.replaceAll("'", "\'");
        sql = sql.replaceAll("\"", "\"");
        return sql;
    }

    //执行增删改
    public Integer operation(String sql) throws SQLException, ClassNotFoundException {
        logger.debug(sql);
        Connection conn = NewDBPUtils.getConnection(dataSource);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int x = pstmt.executeUpdate();
        pstmt.close();
        return x;
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

    public List execute(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException {
        logger.debug(sql);
        List<MainModel> list = new ArrayList<MainModel>();
        if (sql == null) {
            logger.debug(" execute  sql is null");
        } else if (sql.startsWith("select")) {
            list = select(sql);

        } else if (operation(sql) > 0) {


        }
        return list;
    }

    public void release() throws SQLException, ClassNotFoundException {

        NewDBPUtils.release(dataSource);

    }


    public void deleteConn() throws SQLException, ClassNotFoundException {

        release();
    }

    public boolean commit() throws SQLException, ClassNotFoundException {

        NewDBPUtils.commit(dataSource);
        return true;
    }


    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int result = 0;
        if (terms != null && terms.length > 0) {
            String sql = ModelSQLUtils.update(object, terms);
            result = operation(sql);
        }
        return result;
    }


    public List<String> selectOneColList(String sql) throws SQLException, ClassNotFoundException {
        List list = new ArrayList();
        Connection conn = NewDBPUtils.getConnection(dataSource);

        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs;
        rs = pstmt.executeQuery();
        while (rs.next()) {
            String value = rs.getString(1); // 此方法比较高效
            list.add(value);
        }
        rs.close();
        pstmt.close();
        return list;
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

    //查询出列明，数据对应的list集合
    public List<List<MetadataEntity>> select2TempleList(String sql) throws SQLException, ClassNotFoundException {
        logger.debug(sql);
        String tableName="";
        tableName=getTableName(sql);
        List list = new ArrayList();
        Connection conn = NewDBPUtils.getConnection(dataSource);
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

}
