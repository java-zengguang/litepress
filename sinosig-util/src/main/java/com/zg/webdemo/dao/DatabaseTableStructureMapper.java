package com.zg.webdemo.dao;

import com.zg.database.util.JDBCUtils;
import com.zg.database.util.ModelSQLUtils;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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

        String deffSQL="";
         deffSQL=deffSQL+"\n" +
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

     String deffSQL="";
         deffSQL=deffSQL+"select distinct *from (select d.databaseName as databasename,d.tablename ,d.columnname,d.columntype as oldcolumntype,t.columntype from databasetablestructure d ,test t where d.environment ='pro' and d.tablename like t.tablename and d.columnname =t.columnname and batchno='2')a where a.oldcolumntype<>a.columntype\n" +
             "and tablename not in ('PRPTEMMAINLIAB','PRPXMAINLIAB','PRPXMAINCARGO','PRPXINSURED')  order by tablename ";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<DatabaseTableStructureEntity> getProDatabaseTableStructure() throws Exception {
        String sql=" select *from databasetablestructure ";
        return JDBCUtils.select(sql,DatabaseTableStructureEntity.class);
    }

    public List<Map> compareToTable(String environment1,String environment2) throws SQLException {
        String deffSQL=" select '生产库','生产表','生产表字段数','测试库','测试表','测试表字段数' from  dual union \n";
        deffSQL=deffSQL+ "select t3.PDB,t3.PTB,CONCAT(t3.PCN,''),t3.DDB,t3.DTB,CONCAT(t3.DCN,'') from  (\n" +
                "select t1.databasename as 'PDB',t1.tablename as 'PTB',t1.columncount as 'PCN',  t2.databasename as 'DDB',t2.tablename as 'DTB',t2.columncount as 'DCN' from \n" +
                "(select d.databaseName ,d.tablename,count(1) as columncount from databasetablestructure d where d.environment ='"+environment1+"' and d.databaseName <>'' group by d.databaseName ,d.tablename ) t1,\n" +
                "(select d.databaseName ,d.tablename,count(1) as columncount from databasetablestructure d where d.environment ='"+environment2+"' and d.databaseName <>'' group by d.databaseName ,d.tablename) t2\n" +
                "where   t1.databaseName in('投保单库','保单库','批单修改库') and t1.databasename=t2.databasename and t1.tablename=t2.tablename and t1.columncount<> t2.columncount\n" +
                "union all\n" +
                "select '','',0,d.databaseName,d.tablename,count(1) from databasetablestructure d where d.environment ='"+environment1+"'  and d.databaseName in('投保单库','保单库','批单修改库')\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='"+environment2+"' and d.databaseName =d2.databaseName and d2.databaseName in('投保单库','保单库','批单修改库') and  d2.tablename=d.tablename )  group by d.databaseName ,d.tablename\n" +
                "union all\n" +
                "select d.databaseName,d.tablename,count(1),'','',0 from databasetablestructure d where d.environment ='"+environment2+"'  and d.databaseName in('投保单库','保单库','批单修改库')\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='"+environment1+"' and d.databaseName =d2.databaseName and d2.databaseName in('投保单库','保单库','批单修改库') and  d2.tablename=d.tablename )  group by d.databaseName ,d.tablename) t3 ";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareStageExistsPro() throws SQLException {
        String deffSQL="";
         deffSQL=deffSQL+"select d.databaseName,d.tablename from databasetablestructure d where d.environment ='pro' and d.databaseName <>''\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='stage' and  d2.tablename=d.tablename )  group by d.databaseName ,d.tablename ";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareStageExistsProColumn() throws SQLException {
        String deffSQL="";
         deffSQL=deffSQL+"select d.databaseName,d.tablename,d.columnname from databasetablestructure d where d.environment ='pro' and d.databaseName in ('保单库','投保单库','批单修改库') \n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='stage' and d.databaseName=d2.databaseName and d.databaseName in ('保单库','投保单库','批单修改库') and  d2.tablename=d.tablename and d.columnname=d2.columnname) ";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareToColumn(String environment1,String environment2) throws SQLException {
        String deffSQL="select '生产库','生产表','生产字段名','生产字段类型','测试库','测试表','测试字段名','测试字段类型' from  dual union \n"; //表头
         deffSQL=deffSQL+"select t1.databasename as 'PDB',t1.tablename as 'PTB',t1.columnname as 'PCN',t1.columntype as 'PCT', t2.databasename as 'SDB',t2.tablename as 'STB',t2.columnname as 'SCN',t2.columntype as 'SCT' from \n" +
                "(select *  from databasetablestructure d where d.environment ='"+environment1+"') t1,(select *  from databasetablestructure d where d.environment ='"+environment2+"' ) t2\n" +
                "where t1.databasename=t2.databasename and t1.tablename=t2.tablename and t1.columnname=t2.columnname and t1.columntype<>t2.columntype union \n" +
                "select  '' as 'PDB','' as 'PTB','' as 'PCN','' as 'PCT', d.databasename as 'SDB',d.tablename as 'STB',d.columnname as 'SCN',d.columntype as 'SCT' \n" +
                "from databasetablestructure d where d.environment ='"+environment2+"' \n" +
                "and exists (select 1 from databasetablestructure d2 where d2.environment ='"+environment1+"'   and d.databaseName=d2.databaseName and  d2.tablename=d.tablename )\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='"+environment1+"'  and d.databaseName=d2.databaseName and  d2.tablename=d.tablename and d.columnname=d2.columnname) union \n" +
                "select   d.databasename as 'PDB',d.tablename as 'PTB',d.columnname as 'PCN',d.columntype as 'PCT','' as 'SDB','' as 'STB','' as 'SCN','' as 'SCT' \n" +
                "from databasetablestructure d where d.environment ='"+environment1+"' \n" +
                "and exists (select 1 from databasetablestructure d2 where d2.environment ='"+environment2+"'   and d.databaseName=d2.databaseName and  d2.tablename=d.tablename )\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='"+environment2+"'  and d.databaseName=d2.databaseName and  d2.tablename=d.tablename and d.columnname=d2.columnname)";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareToOldTable(String environment) throws SQLException {
        String deffSQL="select '新库','新表','新表字段数','老库','老表','老表字段数' from  dual union \n"; //表头
         deffSQL=deffSQL+"select t3.PDB,t3.PTB,CONCAT(t3.PCN,''),t3.ODB,t3.OTB,CONCAT(t3.OCN,'') from  (\n" +
                 "select t1.databasename as 'PDB',t1.tablename as 'PTB',t1.columncount as 'PCN',  t2.databasename as 'ODB',t2.tablename as 'OTB',t2.columncount as 'OCN' from \n" +
                "(select d.databaseName ,d.tablename,count(1) as columncount from databasetablestructure d where d.environment ='"+environment+"' and d.databaseName <>'' group by d.databaseName ,d.tablename ) t1,\n" +
                "(select d.databaseName ,d.tablename,count(1) as columncount from databasetablestructure d where d.environment ='old' and d.databaseName <>'' group by d.databaseName ,d.tablename) t2\n" +
                "where   t1.tablename=t2.tablename and t1.columncount<> t2.columncount\n" +
                "union all\n" +
                "select '','',0,d.databaseName,d.tablename,count(1) from databasetablestructure d where d.environment ='"+environment+"' \n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='old' and  d2.tablename=d.tablename )  group by d.databaseName ,d.tablename\n" +
                "union all\n" +
                "select d.databaseName,d.tablename,count(1),'','',0 from databasetablestructure d where d.environment ='old'  \n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='"+environment+"' and  d2.tablename=d.tablename )  group by d.databaseName ,d.tablename ) t3";
        return JDBCUtils.selectToMapList(deffSQL);
    }
    public List<Map> compareToOld(String environment) throws SQLException {
        String deffSQL="select '新库','新表','新字段名','新字段类型','老库','老表','老字段名','老字段类型' from  dual union \n";
         deffSQL=deffSQL+"select t1.databasename as 'PDB',t1.tablename as 'PTB',t1.columnname as 'PCN',t1.columntype as 'PCT', t2.databasename as 'ODB',t2.tablename as 'OTB',t2.columnname as 'OCN',t2.columntype as 'OCT' from \n" +
                "(select *  from databasetablestructure d where d.environment ='"+environment+"') t1,(select *  from databasetablestructure d where d.environment ='old' ) t2\n" +
                "where  t1.tablename=t2.tablename and t1.columnname=t2.columnname and t1.columntype<>t2.columntype union \n" +
                "select  '' as 'PDB','' as 'PTB','' as 'PCN','' as 'PCT', d.databasename as 'ODB',d.tablename as 'OTB',d.columnname as 'OCN',d.columntype as 'OCT' \n" +
                "from databasetablestructure d where d.environment ='old' \n" +
                "and exists (select 1 from databasetablestructure d2 where d2.environment ='"+environment+"'    and  d2.tablename=d.tablename )\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='"+environment+"'   and  d2.tablename=d.tablename and d.columnname=d2.columnname) union \n" +
                "select   d.databasename as 'PDB',d.tablename as 'PTB',d.columnname as 'PCN',d.columntype as 'PCT','' as 'ODB','' as 'OTB','' as 'OCN','' as 'OCT' \n" +
                "from databasetablestructure d where d.environment ='"+environment+"' \n" +
                "and exists (select 1 from databasetablestructure d2 where d2.environment ='old'    and  d2.tablename=d.tablename )\n" +
                "and  not exists  (select 1 from databasetablestructure d2 where d2.environment ='old'   and  d2.tablename=d.tablename and d.columnname=d2.columnname)";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> compareT(String environment) throws SQLException {

      /*   String deffSQL="select 'SDN','STN','SCN','SCT','TDN','TTN','TCN','TCT' from  dual union \n";
        deffSQL=deffSQL+"select t1.databasename as 'SDN',t1.tablename as 'STN',t1.columnname as 'SCN',t1.columntype as 'SCT' ,t2.databasename as 'TDN',t2.tablename as 'TTN',t2.columnname as 'TCN',t2.columntype as 'TCT' from \n" +
                "(select distinct REPLACE(REPLACE(REPLACE( REPLACE( REPLACE(t.tablename,'PRPCOPY',''),'PRPCP',''),'PRPP',''),'PRPT',''),'PRPC','') as x,\n" +
                "t.databasename,t.tablename,t.columnname,t.columntype  from (select * from databasetablestructure d where d.environment ='"+environment+"' and d.databaseName in ('保单库','投保单库','批单修改库') and d.tablename REGEXP  'prp[ctp].*') t) t1\n" +
                "inner join \n" +
                "(select distinct REPLACE(REPLACE(REPLACE( REPLACE( REPLACE(t.tablename,'PRPCOPY',''),'PRPCP',''),'PRPP',''),'PRPT',''),'PRPC','') as x,\n" +
                "t.databasename,t.tablename,t.columnname,t.columntype  from (select * from databasetablestructure d where d.environment ='"+environment+"' and d.databaseName in ('保单库','投保单库','批单修改库') and d.tablename REGEXP  'prp[ctp].*') t) t2\n" +
                "on t2.x=t1.x and t1.columnname=t2.columnname  where 1=1 and t1.columntype<>t2.columntype  ";*/
        String deffSQL="select 'SDB','STB','SCN','SCT','TDB','TTB','TCN','TCT' from  dual union \n";
        deffSQL=deffSQL+"select t1.databasename as 'SDB',t1.tablename as 'STB',t1.columnname as 'SCN',t1.columntype as 'SCT',t2.databasename as 'TDB',t2.tablename as 'TTB',t2.columnname as 'TCN',t2.columntype as 'TCT' from\n" +
                "(select t.basetablename,d.* from databasetablestructure d ,(select *from tablerelationship l where l.`type` ='baseModel' ) t where d.environment ='"+environment+"' and   d.databaseName =t.databaseName and   d.tableName =t.tableName ) t1,\n" +
                "(select  t.basetablename,d.*  from databasetablestructure d ,(select *from tablerelationship l where l.`type` ='baseModel') t where  d.environment ='"+environment+"' and   d.databaseName =t.databasename and   d.tableName =t.tableName ) t2\n" +
                "where t1.basetablename=t2.basetablename and t1.columnname=t2.columnname and t1.columntype<>t2.columntype";

        return JDBCUtils.selectToMapList(deffSQL);

    }

    //获取套表里缺少的字段
    public List<Map> compareTColumn(String environment) throws SQLException {

/*        String deffSQL=" select '表名','库名','字段名','字段类型' from  dual union \n ";
        deffSQL=deffSQL+" select *from (select distinct CONCAT(t2.codecode,t1.x) as tablename,t2.codecname as databasename,t1.columnname,t1.columntype from (select distinct REPLACE(REPLACE(REPLACE(REPLACE( REPLACE( REPLACE(t.tablename,'PRPCOPY',''),'PRPCP',''),'PRPP',''),'PRPT',''),'PRPC',''),'ORIGIN','') as x,t.columnname,t.columntype  \n" +
                "from (select * from databasetablestructure d where d.environment ='"+environment+"' and d.databaseName in ('保单库','投保单库','批单修改库') and d.tablename REGEXP  'prp[ctp].*') t ) t1\n" +
                "cross join (select codecode,codecname from ldcode where codetype='prefix') t2 ) t3 where \n" +
                "exists (select 1 from databasetablestructure d2 where d2.environment ='"+environment+"' and d2.databaseName =t3.databaseName and d2.tablename =t3.tablename) \n" +
                "and not exists (select 1 from databasetablestructure d2 where d2.environment ='"+environment+"' and d2.databaseName =t3.databaseName and d2.tablename =t3.tablename and t3.columnname=d2.columnname ) ";
       */

        String deffSQL=" select '表名','库名','字段名','字段类型' from  dual union \n ";
        deffSQL=deffSQL+" \n" +
                "select tablename,databasename,columnname,columntype from  (select t1.basetablename,d1.columnname,max(d1.columntype) as 'columntype' from (select *from tablerelationship t where t.`type` ='baseModel' )t1 ,(select *from databasetablestructure d where d.environment ='"+environment+"') d1\n" +
                "where  t1.tablename=d1.tablename group by  t1.basetablename,d1.columnname )t2,(select *from tablerelationship t where t.`type` ='baseModel' )t3  where 1=1 and t2.basetablename=t3.basetablename\n" +
                "and exists (select *from databasetablestructure d2 where d2.environment ='"+environment+"' and d2.databaseName =t3.databasename and d2.tableName =t3.tablename )\n" +
                "and not exists (select *from databasetablestructure d2 where d2.environment ='"+environment+"' and d2.databaseName =t3.databasename and d2.tableName =t3.tablename and d2.columnName = t2.columnname)";


        return JDBCUtils.selectToMapList(deffSQL);

    }


    public List<Map> compareTTable(String environment) throws SQLException {
/*        String deffSQL=" select '库名','表名','生产库名','生产表名' from  dual union \n ";
        deffSQL=deffSQL+" select codecname ,codecode ,databasename,tablename from \n" +
                "(select *from ldcode l where l.codetype ='prp_table' ) t1 left join \n" +
                "(select distinct databaseName ,tablename from databasetablestructure d2 where d2.environment ='"+environment+"' ) t2\n" +
                "on t2.databaseName =t1.codecname and t2.tablename =t1.codecode where t2.databasename is null ";*/
        String deffSQL=" select '套表名','库名','表名' from  dual union \n ";
        deffSQL=deffSQL+" select t.basetablename ,t.databasename ,t.tablename from tablerelationship t where t.`type` ='baseModel' \n" +
                "and not exists (select 1 from databasetablestructure d where d.environment ='"+environment+"' and d.databaseName =t.databasename and d.tableName =t.tablename ) ";
        return JDBCUtils.selectToMapList(deffSQL);
    }

    public List<Map> getTableNotNullColumn(String sourceEnvironment, String sourceDatabase, String table) throws SQLException {
        List<Map> tableMapList= JDBCUtils.selectToMapList("select *from databasetablestructure d where d.environment ='"+ sourceEnvironment +"' and d.databasename='"+ sourceDatabase +"' and d.nullable = 'N' " +
                "and d.tablename= '"+table+"'");
        return tableMapList;
    }

    public void deleteDatabaseAllStructures() throws IllegalAccessException, ParseException, IOException, InstantiationException, SQLException, ClassNotFoundException {
        JDBCUtils.execute("delete from databasetablestructure ");

    }

    public void deleteDatabaseAllStructures(String environment) throws IllegalAccessException, ParseException, IOException, InstantiationException, SQLException, ClassNotFoundException {
        JDBCUtils.execute("delete from databasetablestructure where environment='"+environment+"' ");

    }

    public List<DatabaseTableStructureEntity> getTableStructure(String environment, String databaseName, String tableName) throws Exception {
        if (tableName.contains(".")) {
            tableName = tableName.substring(tableName.indexOf(".") + 1, tableName.length());
        }
        List<DatabaseTableStructureEntity> tableMapList= JDBCUtils.select("select *from databasetablestructure d where d.environment ='"+ environment +"' and d.databasename='"+ databaseName +"'  " +
                "and d.tablename= '"+tableName+"'",DatabaseTableStructureEntity.class);
        return tableMapList;
   }
}
