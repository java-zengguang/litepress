package io.github.java_zengguang.litepress.db.dao.dao;

import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;
import net.sf.jsqlparser.JSQLParserException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface EntityDao<T> {

    int insertTable(T model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException;

    T insertAutoIncrease(T model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException;
     Integer operation(String sql) throws SQLException, ClassNotFoundException;
     int[] insertTables(List<T> modelLIst, Class<T> modelClass) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException;
    int[] batchSQL(List<String> sqlList) throws SQLException, ClassNotFoundException;
    //插入model_list ，未提交，未初始化连接
     int[] insertTables(List<T> modelList, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException;    //查询
    List<T> select(String sql, String tableName) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException;
    String selectOneValue(String sql) throws SQLException, ClassNotFoundException;
     List<T> select(String sql) throws SQLException, IllegalAccessException, ClassNotFoundException, ParseException, InstantiationException, JSQLParserException, NoSuchMethodException, InvocationTargetException;
    List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException;
     Class<?> selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException;
     Class<?> selectStream(String sql, File tempFile) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException;
    List<String> selectOneColList(String sql) throws SQLException, ClassNotFoundException;
    //查询
     List<T> select(String sql, Class<T> modelClass) throws Exception;

     List<T> select2Page(String sql, Class<T> tClass, PageEntity page) throws Exception;

    public List<T> execute(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException;

    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException;

    public Integer insertMap2Data(String tableName, Map<String, String> para) throws Exception;
}
