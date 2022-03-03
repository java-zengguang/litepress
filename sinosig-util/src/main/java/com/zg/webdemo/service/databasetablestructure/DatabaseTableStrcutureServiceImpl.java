package com.zg.webdemo.service.databasetablestructure;

import com.zg.handler.CommitClassHandler;
import com.zg.sinosig.load.GetDataStructure;
import com.zg.webdemo.dao.DatabaseTableStructureMapper;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;

import java.sql.SQLException;
import java.util.*;

public class DatabaseTableStrcutureServiceImpl extends CommitClassHandler implements DatabaseTableStrcutureService{

    DatabaseTableStructureMapper mapper=new DatabaseTableStructureMapper();

    @Override
    public boolean reloadDataBaseTableStructures(List<DatabaseTableStructureEntity> databaseTableStructureEntityList) throws Exception {
        mapper.deleteDatabaseAllStructures();
        mapper.insertDataBaseTableStructures(databaseTableStructureEntityList);
        return true;
    }

    @Override
    public boolean reloadDataBaseTableStructures(List<DatabaseTableStructureEntity> databaseTableStructureEntityList,String environment,String systemFlag) throws Exception {
        mapper.deleteDatabaseAllStructures( environment, systemFlag);
        mapper.insertDataBaseTableStructures(databaseTableStructureEntityList);
        return true;
    }

    @Override
    public boolean reloadDataBaseTableStructures( String environment,String systemFlag) throws Exception {
        List<DatabaseTableStructureEntity> databaseTableStructureEntityList =new ArrayList();
        List<String> databaseList=new ArrayList();
        switch (systemFlag){
            case "new-non-auto":{
                databaseList=Arrays.asList("保单库","投保单库","批单修改库");
                break;
            }
            case "old-non-auto":{
                databaseList=Arrays.asList("保单库","投保单库");
                break;
            }
            case "platform":{
                databaseList=Arrays.asList("平台库");
                break;
            }
        }
        for(String databaseName:databaseList) {
            databaseTableStructureEntityList.addAll(GetDataStructure.getDataStructure(environment,databaseName,systemFlag));
            mapper.deleteDatabaseAllStructures(environment,databaseName,systemFlag);
            mapper.insertDataBaseTableStructures(databaseTableStructureEntityList);
        }

        return true;
    }

    @Override
    public boolean reloadDataBaseTableStructures(String environment, String databaseName, String systemFlag) throws Exception {
        List<DatabaseTableStructureEntity> databaseTableStructureEntityList =new ArrayList();
        databaseTableStructureEntityList.addAll(GetDataStructure.getDataStructure(environment,databaseName,systemFlag));
        mapper.deleteDatabaseAllStructures(environment,databaseName,systemFlag);
        mapper.insertDataBaseTableStructures(databaseTableStructureEntityList);
        return true;
    }

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
    public Map<String, List<Map>> getCompareResult(String Type) throws SQLException {
        Map<String,List<Map>> result=new HashMap<>();
        switch (Type){
            case "CheckPro":{
                result.put("生产测试表差异对比",mapper.compareToTable("pro","stage"));
                result.put("生产测试字段类型对比",mapper.compareToColumn("pro","stage"));
                result.put("新老核心字段对比",mapper.compareToOld("pro"));
                result.put("新老核心表字段数对比",mapper.compareToOldTable("pro"));
                result.put("生产套表拉齐字段类型对比",mapper.compareT("pro"));
                result.put("生产套表拉齐缺少字段情况",mapper.compareTColumn("pro"));
                result.put("生产套表缺失情况",mapper.compareTTable("pro"));
                break;
            }
            case "CheckStage":{
                result.put("生产套表拉齐字段类型对比",mapper.compareT("stage"));
                result.put("生产套表拉齐缺少字段情况",mapper.compareTColumn("stage"));
                result.put("生产套表缺失情况",mapper.compareTTable("stage"));
                break;
            }

            case "CheckUat":{
                result.put("生产套表拉齐字段类型对比",mapper.compareT("uat"));
                result.put("生产套表拉齐缺少字段情况",mapper.compareTColumn("uat"));
                result.put("生产套表缺失情况",mapper.compareTTable("uat"));
                break;
            }

        }


        return result;
    }

    @Override
    public  List<Map> getCompareResult(String environment,String type) throws SQLException {
        List<Map> result=new ArrayList<>();
        switch (type){
            case "CT":{
               result= mapper.compareT("test");
               break;
            }
            case "CTC":{
                result= mapper.compareTColumn("test");
                break;
            }
            case "CTT":{
                result= mapper.compareTTable("test");
                break;
            }
        }
        return result;
    }


    @Override
    public List<Map> getTableNotNullColumn(String sourceEnvironment, String sourceDatabase, String table) throws SQLException {
        return mapper.getTableNotNullColumn(sourceEnvironment,sourceDatabase,table);
    }

    @Override
    public List<DatabaseTableStructureEntity> getTableStructure(String environment, String databaseName, String tableName,String systemFlag) throws Exception {
        return mapper.getTableStructure(systemFlag,environment,databaseName,tableName);
    }

    @Override
    public List<Map> getKeyColumn(String sourceEnvironment, String sourceDatabase, String systemFlag, String table) throws SQLException {
        return mapper.getTableStructureToMap(sourceEnvironment,sourceDatabase,systemFlag,table);
    }


}
