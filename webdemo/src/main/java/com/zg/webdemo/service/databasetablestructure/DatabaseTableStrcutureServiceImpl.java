package com.zg.webdemo.service.databasetablestructure;

import com.zg.handler.CommitClassHandler;
import com.zg.webdemo.dao.DatabaseTableStructureMapper;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTableStrcutureServiceImpl extends CommitClassHandler implements DatabaseTableStrcutureService{

    DatabaseTableStructureMapper mapper=new DatabaseTableStructureMapper();

    @Override
    public boolean insertDataBaseTableStructures(List<DatabaseTableStructureEntity> databaseTableStructureEntityList,List<String> sqlList) throws Exception {
        try {
            mapper.insertDataBaseTableStructures(databaseTableStructureEntityList);
            mapper.insertDatabaseSqlList(sqlList);
        }  catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
            throw new Exception("異常");
        }
        return true;
    }

    @Override
    public boolean deleteDatabaseTableStrcuture(Map<String, String> map) {
        try {
            mapper.deleteDatabaseTableStrcuture(map);
        } catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<Map> getDiffMap() throws SQLException {
        return mapper.getDiffMap();
    }

    @Override
    public List<Map> getChangMapList() throws SQLException {

        return mapper.getChangMapList();
    }

    public List<DatabaseTableStructureEntity> getProDatabaseTableStructure() throws Exception {
        return mapper.getProDatabaseTableStructure();
    }

    @Override
    public Map<String, List<Map>> getCompareResult() throws SQLException {
        Map<String,List<Map>> result=new HashMap<>();
        result.put("生产测试表差异对比",mapper.compareStagetoPro());
     //   result.put("生产有测试没有的表",mapper.compareStageExistsPro());
        result.put("生产有测试没有的字段",mapper.compareStageExistsProColumn());
        result.put("生产测试字段类型对比",mapper.compareStagetoProColumn());
        result.put("新老核心字段类型对比",mapper.compareOldtoPro());
        result.put("生产套表拉齐字段对比",mapper.compareTtoPro());
        return result;
    }
}
