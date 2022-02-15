package com.zg.webdemo.service.databasetablestructure;

import com.zg.webdemo.entity.DatabaseTableStructureEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseTableStrcutureService {
    boolean reloadDataBaseTableStructures(List<DatabaseTableStructureEntity> databaseTableStructureEntityList) throws Exception;
    boolean reloadDataBaseTableStructures(List<DatabaseTableStructureEntity> databaseTableStructureEntityList,String environment,String systemFlag) throws Exception;
    boolean reloadDataBaseTableStructures(String environment,String systemFlag) throws Exception;

    boolean insertDataBaseTableStructures(List<DatabaseTableStructureEntity> databaseTableStructureEntityList,List<String> sqlList) throws Exception;
    boolean deleteDatabaseTableStrcuture(Map<String,String> map);
    List<Map> getDiffMap() throws SQLException;
    List<Map> getChangMapList() throws SQLException;
    List<DatabaseTableStructureEntity> getProDatabaseTableStructure() throws Exception;
    Map<String,List<Map>> getCompareResult(String Type) throws SQLException;
    List<Map> getCompareResult(String environment,String type) throws SQLException;
    List<Map> getTableNotNullColumn(String sourceEnvironment, String sourceDatabase, String table) throws SQLException;
    List<DatabaseTableStructureEntity> getTableStructure(String environment, String databaseName, String tableName,String systemFlag) throws Exception;
}
