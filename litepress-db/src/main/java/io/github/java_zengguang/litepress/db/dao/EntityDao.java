package io.github.java_zengguang.litepress.db.dao;

import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;

import java.util.List;
import java.util.Map;

public interface EntityDao<T> {


    T insertAutoIncrease(T model) throws Exception;

    Integer insertTable(T model) throws Exception;



    List<String> selectOneColList(String sql) throws Exception;

    //查询
    List<T> select(String sql, Class<T> modelClass) throws Exception;

    List<T> select2Page(String sql, Class<T> tClass, PageEntity page) throws Exception;



    List<Map<String, Object>> selectToMapList(String sql) throws Exception;

    List<MetaDataPo> select2TempleList(String sql, String... tableNames) throws Exception;

    List<Integer> insertTables(List<T> models, String tableName) throws Exception;

    Integer insertEntity(T model, String tableName) throws Exception;

    Integer updateModel(T model, String... terms) throws Exception;

     Integer operation(String sql) throws Exception;




}
