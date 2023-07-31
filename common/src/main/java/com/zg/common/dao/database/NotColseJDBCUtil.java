package com.zg.common.dao.database;

import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.bean.entity.OptionDB;
import com.zg.common.dao.assemble.SimpleAssemble;
import com.zg.common.dao.template.EntityDaoTemplate;
import com.zg.common.dao.template.EntityDaoTemplateFactory;
import com.zg.common.init.Config;
import com.zg.common.util.reflect.DynamicClass;
import com.zg.common.util.reflect.EntityUtils;
import com.zg.common.util.reflect.ModelSQLUtils;
import com.zg.common.util.reflect.SerializeObjectUtils;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class NotColseJDBCUtil {
    private String dataSource;


    public NotColseJDBCUtil(String dataSource) {
        this.dataSource = dataSource;
    }


    private String getTableName(String sql) {
        String stringArray[] = sql.split("\\s+");
        for (int i = 0; i < stringArray.length; i++) {
            if ("from".equals(stringArray[i].toLowerCase().trim()) || "*from".equals(stringArray[i].toLowerCase().trim())) {
                return stringArray[i + 1];
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


    //查询出列明，数据对应的list集合
    private List<List<MetadataEntity>> select2TempleList(String sql, String tableName) throws SQLException, ClassNotFoundException {
        Logger.debug(sql);
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
                    metadataEntity.isNotCommit = "1"; //自增不提交
                } else {
                    metadataEntity.isAutoIncrease = "0";
                    metadataEntity.isNotCommit = "0";  //不自增的列才提交
                }
                //   Logger.info(columnLabel+" "+columnType);
                metadataEntity = entityDaoTemplate.translateEntity(metadataEntity);
                columnList.add(metadataEntity);
            }
            list.add(columnList);
        }
        pstmt.close();
        rs.close();

        return list;
    }

    private int[] insertTables(List modelLIst, Class modelClass) throws SQLException, ClassNotFoundException {
        String tableName = EntityUtils.getTableNameFromModel(modelClass);
        int[] result = new int[0];
        try {
            result = insertTables(modelLIst, modelClass, tableName);
            commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            release();
        }
        return result;
    }


    //查询H2专用
    public List selectNotColse(String sql, String tableName) throws SQLException, ClassNotFoundException {
        List<List<MetadataEntity>> templeList = null;
        List modelList = new ArrayList();
        try {

            templeList = select2TempleList(sql, tableName);
            OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
            SimpleAssemble simpleAssemble = new SimpleAssemble(optionDB.DBType);

            Class modelClass = null;
            if (templeList != null && templeList.size() > 0) {
                modelClass = DynamicClass.getDynamicModel(templeList.get(0));
                for (List<MetadataEntity> columnList : templeList) {
                    Object obj = modelClass.newInstance();
                    for (MetadataEntity metadataEntity : columnList) {
                        obj = simpleAssemble.assembling(metadataEntity, obj);
                    }
                    modelList.add(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return modelList;
    }


    //查询
    public List selectNotColse(String sql) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<List<MetadataEntity>> templeList = null;
        List modelList = new ArrayList();

        templeList = select2TempleList(sql, getTableName(sql));
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        SimpleAssemble simpleAssemble = new SimpleAssemble(optionDB.DBType);
        Class modelClass = null;
        if (templeList != null && templeList.size() > 0) {
            modelClass = DynamicClass.getDynamicModel(templeList.get(0));
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
    public List selectNoClose(String sql, Class modelClass) throws Exception {
        List list = selectToMapList(sql);
        // Map<String, String> tableInfoMap = tableInfo(sql);
        List model_list = SerializeObjectUtils.setMember(list, modelClass);
        return model_list;

    }


    //查询出列明，数据对应的list集合
    private List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {

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
        }
        return list;
    }

    //执行批操作 H2专用
    public int[] batchSqlNoCommit(List<String> sqlList, Boolean model) throws SQLException, ClassNotFoundException {
        int[] result;
        if (!model) {
            result = batchSql(sqlList);
        } else {
            result = batchOneSql(sqlList);
        }
        return result;
    }

    //执行批操作 H2专用 只提交，不关闭链接
    public int[] batchSqlNoColse(List<String> sqlList, Boolean model) throws SQLException, ClassNotFoundException {
        int[] result;
        if (!model) {
            result = batchSql(sqlList);
        } else {
            result = batchOneSql(sqlList);
        }
        commit();
        return result;
    }

    //H2专用
    public void commitAndClose() throws SQLException, ClassNotFoundException {
        commit();
        release();
    }

    //H2专用
    public void closeH2() throws SQLException, ClassNotFoundException {
        release();
    }


}
