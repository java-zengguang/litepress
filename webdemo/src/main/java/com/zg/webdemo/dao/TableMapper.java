package com.zg.webdemo.dao;


import com.zg.database.util.JDBCUtils;
import com.zg.database.util.ModelSQLUtils;
import com.zg.webdemo.entity.PageEntity;
import com.zg.webdemo.entity.Table;
import com.zg.webdemo.util.TableUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkyd01 on 2018/9/1.
 */
public class TableMapper {
    public List searchTableName(Table table) throws Exception {
        String sql=null;

       sql="select table_name as tableName ,table_type as tableType from information_schema.tables " +
               "where 1=1 and table_schema=#{dateBaseName} " +
               "$if{ and table_type=#{tableType} }"+
               "$if{ and table_name LIKE  concat(#{tableName},'%') }  ";

       sql= ModelSQLUtils.dynamicSQL(sql,table);
        List list=JDBCUtils.selectToMapList(sql);
        return list;
    }

    public Integer deleteTableDate(Map map) throws SQLException {
        String sql=null;
        sql="delete from #{tableName} where id=#{id} ";
        try {
            sql= ModelSQLUtils.dynamicSQL(sql,map);
            return JDBCUtils.operation(sql);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
      /*  if(JDBCUtils.operation(sql) &&JDBCUtils.commit()){
            return 1L;
        }*/

        return 0;
    }

    public List getTableDataPage(Map map, PageEntity page) throws Exception {
        String sql=null;

        sql="select * from #{tableName} where 1=1";

        sql=ModelSQLUtils.dynamicSQL(sql,map);
        sql= TableUtil.addPageFromSql(sql,page);
        List list=JDBCUtils.selectToMapList(sql);
        return  list;
    }

    public List<Object> getTableData(HashMap map) throws NoSuchFieldException, IllegalAccessException, SQLException {
        String sql=null;

        sql="select *from #{tableName} where 1=1";

        sql=ModelSQLUtils.dynamicSQL(sql,map);
        List list=JDBCUtils.selectToMapList(sql);
        return list;
    }
}

