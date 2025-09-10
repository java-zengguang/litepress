package io.github.java_zengguang.litepress.db.dao.manager;

import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;
import net.sf.jsqlparser.JSQLParserException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

public interface DataManager {

     List<Map<String,Object>> selectToMapList(String sql) throws SQLException, ClassNotFoundException ;
     List<List<MetadataEntity>> select2TempleList(String sql, String... tableNames) throws SQLException, ClassNotFoundException ;
     Class<?> selectStream(String sql, File tempFile)  throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException, NoSuchMethodException, InvocationTargetException;
     Class<?> selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException, NoSuchMethodException, InvocationTargetException;
     List<List<MetadataEntity>> select2TempleList(String sql) throws SQLException, ClassNotFoundException, JSQLParserException;
     Integer insertEntity(Object model, String tableName, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException, ClassNotFoundException;
     int[] operationAll(List<String> sqlList) throws SQLException, ClassNotFoundException ;
     Integer updateEntity(String dbType, Object model, String... terms) throws IllegalAccessException, InstantiationException, SQLException, ClassNotFoundException;
     int[] batchSQL(List<String> sqlList) throws SQLException, ClassNotFoundException;
     List<String> selectOneColList(String sql) throws SQLException, ClassNotFoundException;
     List<String> batchSqlFile(File file) throws IOException;
     String getOneValue(String sql) throws SQLException, ClassNotFoundException;
     Integer operation(String sql) throws SQLException, ClassNotFoundException ;

}
