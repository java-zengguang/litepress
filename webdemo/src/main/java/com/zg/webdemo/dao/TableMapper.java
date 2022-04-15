package com.zg.webdemo.dao;


import com.zg.database.util.BaseDao;
import com.zg.database.util.ModelSQLUtils;
import com.zg.webdemo.entity.PageEntity;
import com.zg.webdemo.entity.Table;
import com.zg.webdemo.util.TableUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkyd01 on 2018/9/1.
 */
public class TableMapper extends BaseDao {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public List searchTableName(Table table) throws Exception {
        String sql = null;

        sql = "select table_name as tableName ,table_type as tableType from information_schema.tables " +
                "where 1=1 and table_schema=#{dateBaseName} " +
                "$if{ and table_type=#{tableType} }" +
                "$if{ and table_name LIKE  concat(#{tableName},'%') }  ";

        sql = ModelSQLUtils.dynamicSQL(sql, table);
        logger.info(sql);
        List list = selectToMapList(sql);
        return list;
    }

    public Integer deleteTableDate(Map map) throws SQLException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        String sql = null;
        sql = "delete from #{tableName} where id=#{id} ";

        sql = ModelSQLUtils.dynamicSQL(sql, map);
        return operation(sql);


    }

    public List getTableDataPage(Map map, PageEntity page) throws Exception {
        String sql = null;

        sql = "select * from #{tableName} where 1=1";

        sql = ModelSQLUtils.dynamicSQL(sql, map);
        sql = TableUtil.addPageFromSql(sql, page);
        List list = selectToMapList(sql);
        return list;
    }

    public List<Object> getTableData(HashMap map) throws NoSuchFieldException, IllegalAccessException, SQLException, ClassNotFoundException {
        String sql = null;

        sql = "select *from #{tableName} where 1=1";

        sql = ModelSQLUtils.dynamicSQL(sql, map);
        List list = selectToMapList(sql);
        return list;
    }
}

