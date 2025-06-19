package com.zg.common.dao.database;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.github.pagehelper.PageInfo;
import com.zg.common.annotation.AutoIncrease;
import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.bean.entity.OptionDB;
import com.zg.common.bean.entity.PageEntity;
import com.zg.common.dao.assemble.SimpleAssemble;
import com.zg.common.dao.template.EntityDaoTemplate;
import com.zg.common.dao.template.EntityDaoTemplateFactory;
import com.zg.common.init.Config;
import com.zg.common.relect.dynameic.DynameicSerializer;
import com.zg.common.relect.dynameic.DynamicClass;
import com.zg.common.util.database.ParseSQLUtils;
import com.zg.common.util.reflect.*;
import net.sf.jsqlparser.JSQLParserException;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;


public class NewJDBCUtil {
    private final String dataSource;

    private final boolean autoClose;




    public NewJDBCUtil(String dataSource) {
        this.dataSource = dataSource;
        this.autoClose=true;
    }

    public NewJDBCUtil(String dataSource,boolean autoClose) {
        this.dataSource = dataSource;
        this.autoClose=autoClose;
    }

    public void  manualCommitClose() throws SQLException, ClassNotFoundException {
        TransactionManager.commit();
        TransactionManager.release(dataSource);
    }

    public String addPageFromSql(String sql, PageEntity page) throws Exception {
        String countSql = null;
        if (sql != null) {
            countSql = "select count(1) as totalResultSize  from ( " + sql + " ) as num";
        }
        List list = this.selectToMapList(countSql);
        Map map = (Map) list.get(0);
        Integer totalResultSize = Integer.valueOf((String) map.get("totalResultSize"));
        page.setTotalResultSize(totalResultSize);
        page.setTotalPageSize(totalResultSize / page.getPageSize());
        Integer startRows = (page.getCurrentPage() - 1) * page.getPageSize();
        /*  Integer endRows=(page.getCurrentPage())*page.getPageSize();*/
        sql = sql + " limit " + startRows + " , " + page.getPageSize();

        return sql;
    }

    public <T> void convertPage(List<T> listT, PageEntity page) {
        PageInfo<T> resultPage = new PageInfo<T>(listT);
        page.setTotalResultSize(Long.valueOf(resultPage.getTotal()).intValue());
        page.setTotalPageSize(resultPage.getPages());
    }


    private List<List<MetadataEntity>> select2TempleList(String sql) throws SQLException, ClassNotFoundException, JSQLParserException {
        Logger.debug(sql);
        List<String> tableNameList = ParseSQLUtils.parseSelectMainTable(sql);
        String[] array = tableNameList.toArray(new String[0]);
        return select2TempleList(sql, array);

    }


    private List<List<MetadataEntity>> select2TempleList(String sql, String... tableNames) throws SQLException, ClassNotFoundException {
        List list = new ArrayList();
        //合并主表
        StringBuilder tableNameBuffer = new StringBuilder();
        List<String> tableNameList = Arrays.asList(tableNames);
        tableNameList.forEach(tableNameBuffer::append);

        Connection conn = TransactionManager.getConnection(dataSource);
        //获取组件
        List<String> pkColumnList = new ArrayList<>();
        DatabaseMetaData dmd = conn.getMetaData();
        for (String tableName : tableNameList) {
            ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName.toUpperCase());
            while (dmdrs.next()) {
                String pkStr = dmdrs.getString("COLUMN_NAME");
                pkColumnList.add(pkStr);
            }
        }

        //获取数据
        try {
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
                    metadataEntity.ownName = "";
                    metadataEntity.tableName = tableNameBuffer.toString();
                    metadataEntity.columnLabel = columnLabel;
                    metadataEntity.columnType = columnType;
                    metadataEntity.objectValue = columnValue;
                    metadataEntity.columnScale = columnScale;
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
        } catch (Exception e) {
            throw e;
        } finally {
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }


        return list;
    }



    //查询
    public List select(String sql) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, JSQLParserException {
        List modelList = new ArrayList();

        List<List<MetadataEntity>> templeList = select2TempleList(sql);


        return transMetadata2Obj(templeList);
    }


    public List select(String sql, String tableName) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        List modelList = new ArrayList();

        List<List<MetadataEntity>> templeList = select2TempleList(sql, tableName);


        return transMetadata2Obj(templeList);
    }


    private List transMetadata2Obj(List<List<MetadataEntity>> transMetadata2Obj) throws InstantiationException, IllegalAccessException {
        return transMetadata2Obj(transMetadata2Obj,null);
    }

    private List transMetadata2Obj(List<List<MetadataEntity>> templeList,Class modelClass) throws InstantiationException, IllegalAccessException {
        List modelList = new ArrayList();
        if (templeList != null && templeList.size() > 0) {
            OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
            SimpleAssemble simpleAssemble = new SimpleAssemble(optionDB.DBType);
            if (templeList != null && templeList.size() > 0) {
                if (modelClass == null) {
                    modelClass = DynamicClass.getDynamicModel(templeList.get(0));
                }
                for (List<MetadataEntity> columnList : templeList) {
                    Object obj = modelClass.newInstance();
                    for (MetadataEntity metadataEntity : columnList) {
                        if(metadataEntity!=null){
                            obj = simpleAssemble.assembling(metadataEntity, obj);
                        }

                    }
                    modelList.add(obj);
                }
            }
        }
        return modelList;
    }

    //查询

    public List select(String sql, Class modelClass) throws Exception {
        List<List<MetadataEntity>> templeList = null;

        String tableName = DBUtils.getTableNameFromModel(modelClass);

        if (tableName != null) {
            templeList = select2TempleList(sql, tableName);
        } else {
            templeList = select2TempleList(sql);
        }

        return transMetadata2Obj(templeList,modelClass);
    }

    //查询出列明，数据对应的list集合
    public List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {

        // 记录error级别的信息
        Logger.debug(sql);
        List list = new ArrayList();
        try {
            Connection conn = TransactionManager.getConnection(dataSource);
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
            throw e;
        } finally {
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }
        return list;
    }


    public List<String> selectOneColList(String sql) throws SQLException, ClassNotFoundException {
        List list = new ArrayList();
        try {

            Connection conn = TransactionManager.getConnection(dataSource);
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
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }

        return list;
    }


    public String selectOneValue(String sql) throws SQLException, ClassNotFoundException {
        String value = "";
        try {
            Connection conn = TransactionManager.getConnection(dataSource);
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
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }

        return value;
    }


    public Class selectStream(String sql, File tempFile) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException {
        Class modelClass = null;

        String tableName = ParseSQLUtils.parseSelectMainTable(sql).get(0);

        Connection conn = TransactionManager.getConnection(dataSource);
        try {
            //获取组件
            List<String> pkColumnList = new ArrayList<>();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName);
            while (dmdrs.next()) {
                String pkStr = dmdrs.getString("COLUMN_NAME");
                pkColumnList.add(pkStr);
            }


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
                    Integer columnScale = rsmd.getScale(i);
                    if (columnScale == -127) {
                        columnScale = 6;
                    }
                    String columnLabel = rsmd.getColumnLabel(i);
                    String columnType = rsmd.getColumnTypeName(i);
                    Object columnValue = rs.getObject(i);
                    MetadataEntity metadataEntity = new MetadataEntity();
                    metadataEntity.tableName = tableName;
                    metadataEntity.columnLabel = columnLabel;
                    metadataEntity.columnType = columnType;
                    metadataEntity.objectValue = columnValue;
                    metadataEntity.columnScale = columnScale;
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
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }

        return modelClass;
    }


    public Class selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException {
        Class modelClass = null;

        tableName = tableName.trim().toUpperCase();
        String ownName = "";
        if (tableName.contains(".")) {
            String[] splits = tableName.split("\\.");
            ownName = splits[0];
            tableName = splits[1];
        }
        Connection conn = TransactionManager.getConnection(dataSource);
        try {
            //获取组件
            List<String> pkColumnList = new ArrayList<>();
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName.toUpperCase());
            while (dmdrs.next()) {
                String pkStr = dmdrs.getString("COLUMN_NAME");
                pkColumnList.add(pkStr);
            }


            PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            pstmt.setFetchSize(10000);
            pstmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            ResultSet rs;
            rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columncount = 0;
            Kryo kryo = new Kryo();

            OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
            EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(optionDB.DBType);
            Long count = 0L;
            Output output = null;
            while (rs.next()) {
                if (count % fileSize == 0) {
                    if (output != null) {
                        output.close();
                    }
                    File tempFile = new File(tempFileDir, "" + System.currentTimeMillis());
                    tempFile.createNewFile();
                    tempFileList.add(tempFile);
                    output = new Output(new FileOutputStream(tempFile), 1024000);
                }

                List<MetadataEntity> columnList = new ArrayList<>();
                columncount = rsmd.getColumnCount();
                for (int i = 1; i < columncount + 1; i++) {
                    Integer columnScale = rsmd.getScale(i);
                    if (columnScale == -127) {
                        columnScale = 6;
                    }
                    String columnLabel = rsmd.getColumnLabel(i);
                    String columnType = rsmd.getColumnTypeName(i);
                    Object columnValue = rs.getObject(i);
                    MetadataEntity metadataEntity = new MetadataEntity();
                    metadataEntity.ownName = ownName;
                    metadataEntity.tableName = tableName;
                    metadataEntity.columnLabel = columnLabel;
                    metadataEntity.columnType = columnType;
                    metadataEntity.objectValue = columnValue;
                    metadataEntity.columnScale = columnScale;
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
                count++;
            }
            if (output != null) {
                output.close();
            }
            pstmt.close();
            rs.close();
        } catch (Exception e) {
            throw e;
        } finally {
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }

        return modelClass;
    }


    public int[] insertTables(List modelLIst, Class modelClass) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String tableName = DBUtils.getTableNameFromModel(modelClass);
        int[] result = new int[0];
        result = insertTables(modelLIst, modelClass, tableName);

        return result;
    }

    public Object insertAutoIncrease(Object model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        Class clazz = model.getClass();
        Field[] fields = clazz.getFields();
        Field idField = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(AutoIncrease.class)).findFirst().get();
        if (insertTable(model) > 0) {
            String sql = "select @@IDENTITY as id ";
            List<Map> list = selectToMapList(sql);
            Map<String, BigInteger> map = list.get(0);
            Logger.info("id=" + map.get("id").intValue());
            Integer id = map.get("id").intValue();
            idField.set(model, id);
        }
        return model;
    }

    //执行批操作
    public int[] batchSql(List<String> sqlList, Boolean model) throws SQLException, ClassNotFoundException {

        int[] result = new int[0];

        if (!model) {
            result = batchSql(sqlList);
        } else {
            result = batchOneSql(sqlList);
        }

        return result;
    }

    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int result = 0;

        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        if (terms != null && terms.length > 0) {
            String sql = ModelSQLUtils.update(optionDB.getDBType(), object, terms);
            result = operation(sql);

        }

        return result;
    }


    //插入model_list ，未提交，未初始化连接
    private int[] insertTables(List modelList, Class modelClass, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int[] result = null;
        Connection conn = TransactionManager.getConnection(dataSource);

        try {
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
            TransactionManager.commit(dataSource);
        } catch (Exception e) {
            throw e;
        } finally {
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }

        return result;


    }

    private int insertTable(Object model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        List list = new ArrayList();
        list.add(model);
        int[] results = insertTables(list, model.getClass());
        int result = 0;
        if (results != null && results.length > 0) {
            result = results[0];
        }

        return result;
    }


    //执行增删改
    private Integer operation(String sql) throws SQLException, ClassNotFoundException {
        Logger.debug(sql);
        int x = 0;
        Connection conn = TransactionManager.getConnection(dataSource);
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            x = pstmt.executeUpdate();
            pstmt.close();
            TransactionManager.commit(dataSource);
        } catch (Exception e) {
            throw e;
        } finally {
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }
        return x;
    }


    //执行批操作
    private int[] batchSql(List<String> sqlList) throws SQLException, ClassNotFoundException {

        int[] result = new int[sqlList.size()];
        for (int i = 0; i < sqlList.size(); i++) {
            String sql = sqlList.get(i);
            result[i] = operation(sql);
        }

        return result;
    }


    //执行批操作
    public int[] batchOneSql(List<String> sqlList) throws SQLException, ClassNotFoundException {
        //清洗脚本
        for (String sql : sqlList) {
            sql = sql.replace(";", "");
        }

        int[] i = null;
        Statement stmt;
        Connection conn = TransactionManager.getConnection(dataSource);
        try {

            stmt = conn.createStatement();
            Logger.info("--------------start batch-----------");
            for (String sql : sqlList) {
                Logger.info(sql);
                stmt.addBatch(sql);
            }
            i = stmt.executeBatch();
            Logger.info("--------------end batch-----------");
            stmt.close();
            TransactionManager.commit(dataSource);
        } catch (Exception e) {
           throw e;
        } finally {
            if(autoClose) {
                TransactionManager.release(dataSource);
            }
        }

        return i;
    }

}
