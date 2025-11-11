package io.github.java_zengguang.litepress.db.dao.manager;

import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;
import net.sf.jsqlparser.JSQLParserException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

public interface DataManager {

     List<Map<String,Object>> selectToMapList(String sql) throws Exception;
     List<List<MetadataEntity>> select2TempleList(String sql, String... tableNames) throws Exception;
     Class<?> selectStream(String sql, File tempFile) throws Exception;
     Class<?> selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws Exception;
     List<List<MetadataEntity>> select2TempleList(String sql) throws Exception;
     Integer insertEntity(Object model, String tableName, String dbType) throws Exception;
     int[] operationAll(List<String> sqlList) throws Exception;
     Integer updateEntity(String dbType, Object model, String... terms) throws Exception;
     int[] batchSQL(List<String> sqlList) throws Exception;
     List<String> selectOneColList(String sql) throws Exception;
     List<String> batchSqlFile(File file) throws IOException;
     String getOneValue(String sql) throws Exception;
     Integer operation(String sql) throws Exception;

}
