package io.github.java_zengguang.litepress.db.dao;

import io.github.java_zengguang.litepress.core.annotation.AutoIncrease;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;
import io.github.java_zengguang.litepress.core.error.BizException;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.db.dao.assemble.Assemble;
import io.github.java_zengguang.litepress.db.dao.assemble.SimpleAssemble;
import io.github.java_zengguang.litepress.db.dao.factory.ConnectionFactory;
import io.github.java_zengguang.litepress.db.dao.factory.PoolConnectionFactory;
import io.github.java_zengguang.litepress.db.dao.manager.BaseDataManager;
import io.github.java_zengguang.litepress.db.dao.manager.DataManager;
import io.github.java_zengguang.litepress.db.po.EntityDataPo;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;
import io.github.java_zengguang.litepress.db.util.DBUtils;
import io.github.java_zengguang.litepress.db.util.ParseSQLUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class SimpleEntityDao<T> implements EntityDao<T> {
    private String dataSource;
    private ConnectionFactory factory;
    private Assemble<T> assemble;


    public static EntityDao create(String dataSource) throws Exception {
        return new SimpleEntityDao(dataSource);
    }

    public SimpleEntityDao(String dataSource) throws Exception {
        init(dataSource);
    }

    private void init(String dataSource) throws Exception {
        this.dataSource = dataSource;
        this.assemble = new SimpleAssemble<>(((OptionDB) Config.getConfig(dataSource)).dbtype);
        this.factory = PoolConnectionFactory.getInstance();
    }


    public Integer insertTable(T model) throws Exception {
        String tableName = DBUtils.getTableNameFromModel(model.getClass());
        EntityDataPo entityDataPo = assemble.analysis(model);
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            return dataManager.insertEntity(entityDataPo, tableName);
        }
    }


    public T insertAutoIncrease(T model) throws Exception {
        Class<?> clazz = model.getClass();
        Field[] fields = clazz.getFields();
        Field idField = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(AutoIncrease.class))
                .findFirst()
                .orElseThrow(() -> new BizException("未找到 @AutoIncrease 标注的字段: " + clazz.getName()));
        EntityDataPo entityDataPo = assemble.analysis(model);
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            Long id = dataManager.insertEntityReturnKey(entityDataPo, entityDataPo.tableName);
            if (id != null) {
                // 按字段实际类型转换，避免反射 set 抛 IllegalArgumentException。
                // 不要用三元 (int) ? id.intValue() : id，Java 会把两分支提升成 long 再装箱为 Long。
                Class<?> ft = idField.getType();
                Object value;
                if (ft == int.class || ft == Integer.class) {
                    value = id.intValue();   // Integer
                } else {
                    value = id;              // Long
                }
                idField.set(model, value);
            }
        }
        return model;
    }


    public List<T> select(String sql, Class<T> modelClass) throws Exception {
        List<MetaDataPo> metaDataPos = null;

        String tableName = DBUtils.getTableNameFromModel(modelClass);
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            if (tableName != null) {
                metaDataPos = dataManager.select2TempleList(sql, tableName);
            } else {
                List<String> tableNameList = ParseSQLUtils.parseSelectMainTable(sql);
                String[] tableNames = tableNameList.toArray(new String[0]);
                metaDataPos = dataManager.select2TempleList(sql, tableNames);
            }
        }
        return transMetadata2Obj(metaDataPos, modelClass);
    }


    @Override
    public List<String> selectOneColList(String sql) throws Exception {
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            List<String> datas = dataManager.selectOneColList(sql);
            return datas;
        }
    }


    private List<T> transMetadata2Obj(List<MetaDataPo> metaDataPos, Class<T> modelClass) throws
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<T> modelList = new ArrayList<>();
        if (metaDataPos != null && !metaDataPos.isEmpty()) {
            for (MetaDataPo metaDataPo : metaDataPos) {
                T obj = modelClass.getDeclaredConstructor().newInstance();
                obj = assemble.assembling(metaDataPo, obj);
                modelList.add(obj);
            }
        }
        return modelList;
    }


    public List<T> select2Page(String sql, Class<T> tClass, PageEntity page) throws Exception {
        String countSql = null;
        if (sql != null) {
            countSql = "select count(1) as totalResultSize  from ( " + sql + " ) as num";
        }
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            List<Map<String, Object>> contMapList = dataManager.selectToMapList(countSql);
            Map<String, Object> map = contMapList.getFirst();
            Integer totalResultSize = Math.toIntExact((Long) map.get("totalResultSize"));
            page.setTotalResultSize(totalResultSize);
            page.setTotalPageSize((totalResultSize / page.getPageSize()));
            Integer startRows = (page.getCurrentPage() - 1) * page.getPageSize();
            /*  Integer endRows=(page.getCurrentPage())*page.getPageSize();*/
            sql = sql + " limit " + startRows + " , " + page.getPageSize();
        }
        return select(sql, tClass);
    }


    @Override
    public List<Map<String, Object>> selectToMapList(String sql) throws Exception {
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            return dataManager.selectToMapList(sql);
        }
    }

    @Override
    public List<MetaDataPo> select2TempleList(String sql, String... tableNames) throws Exception {
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            return dataManager.select2TempleList(sql, tableNames);
        }
    }

    @Override
    public List<Integer> insertTables(List<T> models, String tableName) throws Exception {
        List<EntityDataPo> entityDataPos = assemble.analysisList(models);
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            return dataManager.insertTables(entityDataPos, tableName);
        }

    }

    @Override
    public Integer insertEntity(T model, String tableName) throws Exception {
        EntityDataPo entityDataPo = assemble.analysis(model);
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            return dataManager.insertEntity(entityDataPo, tableName);
        }
    }

    @Override
    public Integer updateModel(T model, String... terms) throws Exception {
        EntityDataPo entityDataPo = assemble.analysis(model);
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            return dataManager.updateModel(entityDataPo, terms);
        }
    }

    @Override
    public Integer operation(String sql) throws Exception {
        try (Connection conn = factory.createConnection(dataSource)) {
            DataManager dataManager = new BaseDataManager(conn);
            return dataManager.operation(sql);
        }

    }


}
