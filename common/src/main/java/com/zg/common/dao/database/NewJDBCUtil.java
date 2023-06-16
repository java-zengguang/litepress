package com.zg.common.dao.database;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.zg.common.annotation.AutoIncrease;
import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.bean.entity.OptionDB;
import com.zg.common.dao.assemble.SimpleAssemble;
import com.zg.common.dao.template.EntityDaoTemplate;
import com.zg.common.dao.template.EntityDaoTemplateFactory;
import com.zg.common.init.Config;
import com.zg.common.util.reflect.*;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;


public class NewJDBCUtil {
    private String dataSource;


    public NewJDBCUtil(String dataSource) {
        this.dataSource = dataSource;
    }


    private String getTableName(String sql) {
        String stringArray[] = sql.split("\\s+");
        for (int i = 0; i < stringArray.length; i++) {
            if ("FROM".equals(stringArray[i].toUpperCase().trim()) || "*FROM".equals(stringArray[i].toUpperCase().trim())) {
                return stringArray[i + 1].toUpperCase();
            }
        }
        Logger.debug(" getTableName   未找到tableName");
        return null;
    }

    //插入model_list ，未提交，未初始化连接
    private int[] insertTables(List modelList, Class modelClass, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int[] result = null;
        Connection conn = NewDBPUtils.getConnection(dataSource);
        Statement stmt = conn.createStatement();
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        Logger.debug("-----------------start batch-----------");
        for (Object model : modelList) {
            String sql = ModelSQLUtils.insert(model, tableName, optionDB.DBType);
            Logger.info(sql);

            stmt.addBatch(sql);

        }
        Logger.debug("------------------end batch-------------");
        result = stmt.executeBatch();
        stmt.close();
        return result;


    }

    private int insertTable(Object model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        List list = new ArrayList();
        list.add(model);
        int results[] = insertTables(list, model.getClass());
        int result = 0;
        if (results != null && results.length > 0) {
            result = results[0];
        }

        return result;
    }

/*
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
*/

    //执行增删改
    private Integer operation(String sql) throws SQLException, ClassNotFoundException {
        Logger.debug(sql);
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


    private void release() throws SQLException, ClassNotFoundException {
        NewDBPUtils.release(dataSource);
    }


    private boolean commit() throws SQLException, ClassNotFoundException {
        NewDBPUtils.commit(dataSource);
        return true;
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
        Logger.info("--------------start batch-----------");
        for (String sql : sqlList) {
            Logger.info(sql);
            stmt.addBatch(sql);
        }
        i = stmt.executeBatch();
        Logger.info("--------------end batch-----------");
        stmt.close();

        return i;
    }


    private List<List<MetadataEntity>> select2TempleList(String sql) throws SQLException, ClassNotFoundException {
        String tableName = "";
        //获取表名
        tableName = getTableName(sql);
        return select2TempleList(sql, tableName);
    }

    //查询出列明，数据对应的list集合
    private List<List<MetadataEntity>> select2TempleList(String sql, String tableName) throws SQLException, ClassNotFoundException {
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
                Object columnValue = rs.getObject(i);
                MetadataEntity metadataEntity = new MetadataEntity();
                metadataEntity.ownName = ownName;
                metadataEntity.tableName = tableName;
                metadataEntity.columnLabel = columnLabel;
                metadataEntity.columnType = columnType;
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

    public int[] insertTables(List modelLIst, Class modelClass) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String tableName = EntityUtils.getTableNameFromModel(modelClass);
        int[] result = new int[0];
        try {
            result = insertTables(modelLIst, modelClass, tableName);
            commit();
        } catch (Exception e) {
            throw e;
        } finally {
            release();
        }
        return result;
    }
    public Object insertAutoIncrease(Object model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        Class clazz=model.getClass();
        Field[] fields= clazz.getFields();
        Field idField= Arrays.stream(fields).filter(field -> field.isAnnotationPresent(AutoIncrease.class)).findFirst().get();
        if(insertTable(model)>0){
            String sql = "select @@IDENTITY as id ";
            List<Map> list = selectToMapList(sql);
            Map<String, BigInteger> map = list.get(0);
            Logger.info("id=" + map.get("id").intValue());
            Integer id = Integer.valueOf(map.get("id").intValue());
            idField.set(model,id);
        }
        return model;
    }

    //查询
    public List select(String sql) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<List<MetadataEntity>> templeList = null;
        List modelList = new ArrayList();
        try {
            templeList = select2TempleList(sql);
            if (templeList != null && templeList.size() > 0) {
                OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
                SimpleAssemble simpleAssemble = new SimpleAssemble(optionDB.DBType);
                Class modelClass = null;
                if (templeList != null && templeList.size() > 0) {
                    if (modelClass == null) {
                        modelClass = DynamicClass.getDynamicModel(templeList.get(0));
                    }
                    for (List<MetadataEntity> columnList : templeList) {
                        Object obj = modelClass.newInstance();
                        for (MetadataEntity metadataEntity : columnList) {
                            obj = simpleAssemble.assembling(metadataEntity, obj);
                        }
                        modelList.add(obj);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            release();
        }

        return modelList;
    }


    public List select(String sql, String tableName) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<List<MetadataEntity>> templeList = null;
        List modelList = new ArrayList();
        try {
            templeList = select2TempleList(sql, tableName);

            OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
            SimpleAssemble simpleAssemble = new SimpleAssemble(optionDB.DBType);

            Class modelClass = null;
            if (templeList != null && templeList.size() > 0) {
                if (modelClass == null) {
                    modelClass = DynamicClass.getDynamicModel(templeList.get(0));
                }
                for (List<MetadataEntity> columnList : templeList) {
                    Object obj = modelClass.newInstance();
                    for (MetadataEntity metadataEntity : columnList) {
                        obj = simpleAssemble.assembling(metadataEntity, obj);
                    }
                    modelList.add(obj);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            release();
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


    //查询出列明，数据对应的list集合
    public List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {

        // 记录error级别的信息
        Logger.debug(sql);
        List list = new ArrayList();
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            release();
        }
        return list;
    }

    //执行批操作
    public int[] batchSql(List<String> sqlList, Boolean model) throws SQLException, ClassNotFoundException {

        int[] result = new int[0];
        try {
            if (!model) {
                result = batchSql(sqlList);
            } else {
                result = batchOneSql(sqlList);
            }
            commit();
        } catch (SQLException e) {
            throw e;
        } finally {
            release();
        }
        return result;
    }

    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int result = 0;
        try {
            OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
            if (terms != null && terms.length > 0) {
                String sql = ModelSQLUtils.update(optionDB.getDBType(), object, terms);
                result = operation(sql);
                commit();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            release();
        }

        return result;
    }


    public List<String> selectOneColList(String sql) throws SQLException, ClassNotFoundException {
        List list = new ArrayList();
        try {


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
        } catch (Exception e) {
            throw e;
        } finally {
            release();
        }

        return list;
    }


    public String selectOneValue(String sql) throws SQLException, ClassNotFoundException {
        String value = "";
        try {


            Connection conn = NewDBPUtils.getConnection(dataSource);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs;
            rs = pstmt.executeQuery();
            while (rs.next()) {
                value = rs.getString(1); // 此方法比较高效

            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            throw e;
        } finally {
            release();
        }

        return value;
    }


    public Class selectStream(String sql, File tempFile) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
        Class modelClass = null;
        try {
            String tableName = "";
            tableName = getTableName(sql);
            if (tableName.contains(".")) {
                tableName = tableName.substring(tableName.indexOf("."), tableName.length());
            }
            Connection conn = NewDBPUtils.getConnection(dataSource);
            PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            pstmt.setFetchSize(10000);
            pstmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            ResultSet rs;
            rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columncount = 0;
            Output output = new Output(new FileOutputStream(tempFile), 1024000);
            Kryo kryo = new Kryo();

            OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
            EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(optionDB.DBType);
            while (rs.next()) {
                List<MetadataEntity> columnList = new ArrayList<>();
                columncount = rsmd.getColumnCount();
                for (int i = 1; i < columncount + 1; i++) {
                    String columnLabel = rsmd.getColumnLabel(i);
                    String columnType = rsmd.getColumnTypeName(i);
                    Object columnValue = rs.getObject(i);
                    MetadataEntity metadataEntity = new MetadataEntity();
                    metadataEntity.tableName = tableName;
                    metadataEntity.columnLabel = columnLabel;
                    metadataEntity.columnType = columnType;
                    metadataEntity.objectValue = columnValue;
                    metadataEntity = entityDaoTemplate.translateEntity(metadataEntity);
                    columnList.add(metadataEntity);
                }
                SimpleAssemble simpleAssemble = new SimpleAssemble(optionDB.DBType);
                if (modelClass == null) {
                    modelClass = DynamicClass.getDynamicModel(columnList);
                    kryo.register(modelClass, new DynameicSerializer(modelClass));
                }

                Object obj = modelClass.newInstance();
                for (MetadataEntity metadataEntity : columnList) {
                    obj = simpleAssemble.assembling(metadataEntity, obj);
                }
                kryo.writeObject(output, obj);
            }
            rs.close();
            pstmt.close();
            output.close();
        } catch (Exception e) {
            throw e;
        } finally {
            release();
        }

        return modelClass;
    }



}
