package com.zg.webdemo.dao;

import com.zg.database.util.JDBCUtils;
import com.zg.database.util.ModelSQLUtils;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseTableStructureMapper {

    public void insertDataBaseTableStructures(List<DatabaseTableStructureEntity> databaseTableStructureEntityList) throws SQLException, IllegalAccessException {
        System.out.println("载入Entity模型");
        JDBCUtils.insertTables(databaseTableStructureEntityList, DatabaseTableStructureEntity.class);

    }

    public void insertDatabaseSqlList(List<String> list) throws SQLException {
        System.out.println("载入SLQ脚本");
        JDBCUtils.batchSql(list);
    }
    public void deleteDatabaseTableStrcuture(Map<String, String> map) throws NoSuchFieldException, IllegalAccessException, SQLException {

        String sql = null;
        sql = "delete from #{tableName} where id=#{id} ";

        sql = ModelSQLUtils.dynamicSQL(sql, map);
        JDBCUtils.operation(sql);
    }

    public List<Map<String,String>> getCompareList(Map<String,String> map){
        return null;
    }

    public List<Map> getDiffMap() throws SQLException {

        String deffSQL="\n" +
                "select t1.databaseName,\n" +
                "       t1.tablename,\n" +
                "       t1.columnname,\n" +
                "       t1.columntype   as stageColumnType,\n" +
                "       t2.columntype   as proColumnType\n" +
                "  from (select * from databasetablestructure d1 where d1.environment = 'pro') t1,\n" +
                "       (select *\n" +
                "          from databasetablestructure d1\n" +
                "         where d1.environment = 'stage') t2\n" +
                " where t1.databasename = t2.databasename\n" +
                "   and t1.tablename = t2.tablename\n" +
                "   and t1.columnname = t2.columnname\n" +
                "   and t1.columntype <> t2.columntype\n" +
                "union all\n" +
                "select d1.databaseName, d1.tablename, d1.columnname, d1.columntype, ''\n" +
                "  from databasetablestructure d1\n" +
                " where d1.environment = 'pro'\n" +
                "   and not exists (select *\n" +
                "          from databasetablestructure d2\n" +
                "         where d2.environment = 'stage'\n" +
                "           and d1.databaseName = d2.databasename\n" +
                "           and d1.tablename = d2.tablename\n" +
                "           and d1.columnname = d2.columnname)\n" +
                "union all\n" +
                "select d1.databaseName, d1.tablename, d1.columnname, '', d1.columntype\n" +
                "  from databasetablestructure d1\n" +
                " where d1.environment = 'stage'\n" +
                "   and not exists (select *\n" +
                "          from databasetablestructure d2\n" +
                "         where d2.environment = 'pro'\n" +
                "           and d1.databaseName = d2.databasename\n" +
                "           and d1.tablename = d2.tablename\n" +
                "           and d1.columnname = d2.columnname)\n";
       return JDBCUtils.selectToMapList(deffSQL);

    }

    public List<Map> getChangMapList() throws SQLException {

     String deffSQL="select distinct *from (select d.databaseName as databasename,d.tablename ,d.columnname,d.columntype as oldcolumntype,t.columntype from databasetablestructure d ,test t where d.environment ='pro' and d.tablename like t.tablename and d.columnname =t.columnname and batchno='2')a where a.oldcolumntype<>a.columntype\n" +
             "and tablename not in ('PRPTEMMAINLIAB','PRPXMAINLIAB','PRPXMAINCARGO','PRPXINSURED')  order by tablename ";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<DatabaseTableStructureEntity> getProDatabaseTableStructure() throws Exception {
        String sql=" select *from databasetablestructure ";
        return JDBCUtils.select(sql,DatabaseTableStructureEntity.class);
    }

    public List<Map> compareStagetoPro() throws SQLException {
        String deffSQL="select t1.databasename as '生产库',t1.tablename as '生产表',t1.columncount as '生产表字段数',  t2.databasename as '测试库',t2.tablename as '测试表',t2.columncount as '测试表字段数' from \n" +
                "(select d.databaseName ,d.tablename,count(1) as columncount from databasetablestructure d where d.environment ='pro' and d.databaseName <>'' group by d.databaseName ,d.tablename ) t1,\n" +
                "(select d.databaseName ,d.tablename,count(1) as columncount from databasetablestructure d where d.environment ='stage' and d.databaseName <>'' group by d.databaseName ,d.tablename) t2\n" +
                "where   t1.databaseName in('投保单库','保单库','批单修改库') and t1.databasename=t2.databasename and t1.tablename=t2.tablename and t1.columncount<> t2.columncount\n" +
                "union all\n" +
                "select '','',0,d.databaseName,d.tablename,count(1) from databasetablestructure d where d.environment ='pro'  and d.databaseName in('投保单库','保单库','批单修改库')\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='stage' and d.databaseName =d2.databaseName and d2.databaseName in('投保单库','保单库','批单修改库') and  d2.tablename=d.tablename )  group by d.databaseName ,d.tablename\n" +
                "union all\n" +
                "select d.databaseName,d.tablename,count(1),'','',0 from databasetablestructure d where d.environment ='stage'  and d.databaseName in('投保单库','保单库','批单修改库')\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='pro' and d.databaseName =d2.databaseName and d2.databaseName in('投保单库','保单库','批单修改库') and  d2.tablename=d.tablename )  group by d.databaseName ,d.tablename\n";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareStageExistsPro() throws SQLException {
        String deffSQL="select d.databaseName,d.tablename from databasetablestructure d where d.environment ='pro' and d.databaseName <>''\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='stage' and  d2.tablename=d.tablename )  group by d.databaseName ,d.tablename ";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareStageExistsProColumn() throws SQLException {
        String deffSQL="select d.databaseName,d.tablename,d.columnname from databasetablestructure d where d.environment ='pro' and d.databaseName in ('保单库','投保单库','批单修改库') \n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='stage' and d.databaseName=d2.databaseName and d.databaseName in ('保单库','投保单库','批单修改库') and  d2.tablename=d.tablename and d.columnname=d2.columnname) ";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareStagetoProColumn() throws SQLException {
        String deffSQL="select t1.databasename as '生产库',t1.tablename as '生产表',t1.columnname as '生产字段名',t1.columntype as '生产字段类型', t2.databasename as '测试库',t2.tablename as '测试表',t2.columnname as '测试字段名',t2.columntype as '测试字段类型' from \n" +
                "(select *  from databasetablestructure d where d.environment ='pro') t1,(select *  from databasetablestructure d where d.environment ='stage' ) t2\n" +
                "where t1.databasename=t2.databasename and t1.tablename=t2.tablename and t1.columnname=t2.columnname and t1.columntype<>t2.columntype";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareOldtoPro() throws SQLException {
        String deffSQL="select t1.databasename as '生产库',t1.tablename as '新表',t1.columnname as '新字段名',t1.columntype as '新字段类型', t2.databasename as '老库',t2.tablename as '老表',t2.columnname as '老字段名',t2.columntype as '老字段类型' from \n" +
                "(select *  from databasetablestructure d where d.environment ='pro') t1,(select *  from databasetablestructure d where d.environment ='old' ) t2\n" +
                "where  t1.tablename=t2.tablename and t1.columnname=t2.columnname and t1.columntype<>t2.columntype";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareTtoPro() throws SQLException {
        String deffSQL="select * from  (select REPLACE(d.tablename ,'PRPT','') as x,d.*from databasetablestructure d where d.environment ='pro' and d.tablename like 'PRPT%') t1 ,( select REPLACE(d.tablename ,'PRPC','') as x,d.*from databasetablestructure d where d.environment ='pro' and d.tablename like 'PRPC%') t2 where t1.x=t2.x and t1.columnname=t2.columnname and t1.columntype<>t2.columntype\n" +
                "union \n" +
                "select * from  (select REPLACE(d.tablename ,'PRPT','') as x,d.*from databasetablestructure d where d.environment ='pro' and d.tablename like 'PRPT%') t1 ,( select REPLACE(d.tablename ,'PRPCP','') as x,d.*from databasetablestructure d where d.environment ='pro' and d.tablename like 'PRPCP%') t2 where t1.x=t2.x and t1.columnname=t2.columnname and t1.columntype<>t2.columntype\n" +
                "union \n" +
                "select * from  (select REPLACE(d.tablename ,'PRPT','') as x,d.*from databasetablestructure d where d.environment ='pro' and d.tablename like 'PRPT%') t1 ,( select REPLACE(d.tablename ,'PRPCOPY','') as x,d.*from databasetablestructure d where d.environment ='pro' and d.tablename like 'PRPCOPY%') t2 where t1.x=t2.x and t1.columnname=t2.columnname and t1.columntype<>t2.columntype\n" +
                "union \n" +
                "select * from  (select REPLACE(d.tablename ,'PRPT','') as x,d.*from databasetablestructure d where d.environment ='pro' and d.tablename like 'PRPT%') t1 ,( select REPLACE(d.tablename ,'PRPP','') as x,d.*from databasetablestructure d where d.environment ='pro' and d.tablename like 'PRPP%') t2 where t1.x=t2.x and t1.columnname=t2.columnname and t1.columntype<>t2.columntype\n";
        return JDBCUtils.selectToMapList(deffSQL);
    }
}
