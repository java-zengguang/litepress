package com.zg.webdemo.service.databasetablestructure;

import com.zg.webdemo.entity.DatabaseTableStructureEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseTableStrcutureService {
    boolean insertDataBaseTableStructures(List<DatabaseTableStructureEntity> databaseTableStructureEntityList,List<String> sqlList) throws Exception;
    boolean deleteDatabaseTableStrcuture(Map<String,String> map);
    List<Map> getDiffMap() throws SQLException;
    List<Map> getChangMapList() throws SQLException;
    List<DatabaseTableStructureEntity> getProDatabaseTableStructure() throws Exception;
    Map<String,List<Map>> getCompareResult() throws SQLException;
}
