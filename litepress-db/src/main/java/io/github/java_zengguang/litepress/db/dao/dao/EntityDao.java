package io.github.java_zengguang.litepress.db.dao.dao;

import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface EntityDao<T> {


    T insertAutoIncrease(T model) throws Exception;

    int[] insertTables(List<T> modelLIst, Class<T> modelClass) throws Exception;

    int[] insertTables(List<T> modelList, String tableName) throws Exception;    //查询

    int insertTable(T model) throws Exception;

    Integer insertMap2Data(String tableName, Map<String, String> para) throws Exception;


    Integer operation(String sql) throws Exception;

    int[] batchSQL(List<String> sqlList) throws Exception;

    List<T> select(String sql, String tableName) throws Exception;

    String selectOneValue(String sql) throws Exception;

    List<T> select(String sql) throws Exception;

    List<Map> selectToMapList(String sql) throws Exception;

    Class<?> selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws Exception;

    Class<?> selectStream(String sql, File tempFile) throws Exception;

    List<String> selectOneColList(String sql) throws Exception;

    //查询
    List<T> select(String sql, Class<T> modelClass) throws Exception;

    List<T> select2Page(String sql, Class<T> tClass, PageEntity page) throws Exception;

    List<T> execute(String sql) throws Exception;

    int updateModel(Object object, String... terms) throws Exception;



}
