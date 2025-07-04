package io.github.java_zengguang.litepress.db.dao.dao;

import io.github.java_zengguang.litepress.core.annotation.AutoIncrease;
import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.core.relect.dynameic.DynamicClass;
import io.github.java_zengguang.litepress.db.dao.assemble.SimpleAssemble;
import io.github.java_zengguang.litepress.db.dao.manager.BaseDataManager;
import io.github.java_zengguang.litepress.db.dao.manager.DataManager;
import io.github.java_zengguang.litepress.db.util.DBUtils;
import io.github.java_zengguang.litepress.db.util.ModelSQLUtils;
import net.sf.jsqlparser.JSQLParserException;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;


public class BaseEntityDao<T> implements EntityDao<T> {
    private DataManager dataManager;
    private OptionDB optionDB;

    public BaseEntityDao() {
        this.optionDB = (OptionDB) Config.getConfig("optionDB");
        this.dataManager = new BaseDataManager("optionDB");
    }

    public BaseEntityDao(String dataSource) {
        this.dataManager = new BaseDataManager(dataSource);
        this.optionDB = (OptionDB) Config.getConfig(dataSource);
    }

    public int insertTable(T model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        List<T> list = new ArrayList<>();
        list.add(model);
        int[] results = insertTables(list, (Class<T>) model.getClass());
        int result = 0;
        if (results != null && results.length > 0) {
            result = results[0];
        }
        return result;
    }

    public Integer operation(String sql) throws SQLException, ClassNotFoundException {
        return dataManager.operation(sql);
    }

    public T insertAutoIncrease(T model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        Class<?> clazz = model.getClass();
        Field[] fields = clazz.getFields();
        Field idField = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(AutoIncrease.class)).findFirst().get();
        if (insertTable(model) > 0) {
            String sql = "select @@IDENTITY as id ";
            List<Map<String, Object>> list = dataManager.selectToMapList(sql);
            Map<String, Object> map = list.getFirst();
            Object id = map.get("id");
            if (id instanceof BigInteger) {
                id = ((BigInteger) id).intValue();
            }
            idField.set(model, id);
        }
        return model;
    }

    public int[] insertTables(List<T> modelLIst, Class<T> modelClass) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        String tableName = DBUtils.getTableNameFromModel(modelClass);
        return insertTables(modelLIst, tableName);
    }

    @Override
    public int[] batchSQL(List<String> sqlList) throws SQLException, ClassNotFoundException {
        return dataManager.batchSQL(sqlList);
    }

    //插入model_list ，未提交，未初始化连接
    public int[] insertTables(List<T> modelList, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        List<String> sqlList = modelList.stream().map((model) -> {
            try {
                return ModelSQLUtils.insert(model, tableName, optionDB.dbtype);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();

        return dataManager.batchSQL(sqlList);

    }


    public List select(String sql, Class modelClass) throws Exception {
        List<List<MetadataEntity>> templeList = null;

        String tableName = DBUtils.getTableNameFromModel(modelClass);

        if (tableName != null) {
            templeList = dataManager.select2TempleList(sql, tableName);
        } else {
            templeList = dataManager.select2TempleList(sql);
        }

        return transMetadata2Obj(templeList, modelClass);
    }

    //查询
    public List<T> select(String sql) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException {
        List<List<MetadataEntity>> templeList = dataManager.select2TempleList(sql);
        return transMetadata2Obj(templeList);
    }


    public List<T> select(String sql, String tableName) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

        List<List<MetadataEntity>> templeList = dataManager.select2TempleList(sql, tableName);

        return transMetadata2Obj(templeList);
    }


    //查询出列明，数据对应的list集合
    public List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {

        // 记录error级别的信息
        Logger.debug(sql);
        List list = dataManager.selectToMapList(sql);
        return list;
    }


    public List<String> selectOneColList(String sql) throws SQLException, ClassNotFoundException {
        return dataManager.selectOneColList(sql);
    }

    private List<T> transMetadata2Obj(List<List<MetadataEntity>> transMetadata2Obj) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return transMetadata2Obj(transMetadata2Obj, null);
    }

    private List<T> transMetadata2Obj(List<List<MetadataEntity>> templeList, Class<T> modelClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<T> modelList = new ArrayList<>();
        if (templeList != null && !templeList.isEmpty()) {
            SimpleAssemble<T> simpleAssemble = new SimpleAssemble<>(optionDB.dbtype);
            if (!templeList.isEmpty()) {
                if (modelClass == null) {
                    modelClass = DynamicClass.getDynamicModel(templeList.get(0));
                }
                for (List<MetadataEntity> columnList : templeList) {
                    T obj = modelClass.getDeclaredConstructor().newInstance();
                    for (MetadataEntity metadataEntity : columnList) {
                        if (metadataEntity != null) {
                            obj = simpleAssemble.assembling(metadataEntity, obj);
                        }

                    }
                    modelList.add(obj);
                }
            }
        }
        return modelList;
    }


    public String selectOneValue(String sql) throws SQLException, ClassNotFoundException {
        return dataManager.selectOneColList(sql).getFirst();
    }


    public Class<?> selectStream(String sql, File tempFile) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException {
        return dataManager.selectStream(sql, tempFile);
    }


    public Class<?> selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException {
        return dataManager.selectStream(sql, tableName, tempFileDir, tempFileList, fileSize);
    }


    public List<T> select2Page(String sql, Class<T> tClass, PageEntity page) throws Exception {
        String countSql = null;
        if (sql != null) {
            countSql = "select count(1) as totalResultSize  from ( " + sql + " ) as num";
        }
        List<Map<String, Object>> contMapList = dataManager.selectToMapList(countSql);
        Map<String, Object> map = contMapList.getFirst();
        Integer totalResultSize = Math.toIntExact((Long) map.get("totalResultSize"));
        page.setTotalResultSize(totalResultSize);
        page.setTotalPageSize((totalResultSize / page.getPageSize()));
        Integer startRows = (page.getCurrentPage() - 1) * page.getPageSize();
        /*  Integer endRows=(page.getCurrentPage())*page.getPageSize();*/
        sql = sql + " limit " + startRows + " , " + page.getPageSize();
        return select(sql, tClass);
    }

    public List<T> execute(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException {
        Logger.debug(sql);
        List<T> list = new ArrayList<>();
        if (sql == null) {
            Logger.debug(" execute  sql is null");
        } else if (sql.startsWith("select")) {
            list = select(sql);
        } else {
            dataManager.operation(sql);
        }
        return list;
    }


    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int result = 0;
        if (terms != null && terms.length > 0) {
            String sql = ModelSQLUtils.update(optionDB.dbtype, object, terms);
            result = dataManager.operation(sql);
        }
        return result;
    }


    public Integer insertMap2Data(String tableName, Map<String, String> para) throws Exception {
        String sql = "insert into " + tableName;
        StringBuilder column = new StringBuilder();
        StringBuilder values = new StringBuilder();
        Set<String> columnSet = para.keySet();
        for (String c : columnSet) {
            column.insert(0, c + " ,");
            values = new StringBuilder(para.get(c) + " ," + "'" + values + "'");
        }
        if (column.toString().endsWith(",")) {
            column = new StringBuilder(column.substring(0, column.length() - 1));
        }
        if (values.toString().endsWith(",")) {
            values = new StringBuilder(values.substring(0, values.length() - 1));
        }
        sql = sql + " (" + column + ")" + " values (" + values + ")";
        return dataManager.operation(sql);

    }


}
