package com.zg.io.dao;


import com.zg.database.util.JDBCUtils;
import com.zg.database.util.ModelSQLUtils;
import com.zg.io.entity.FileEntity;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkyd01 on 2018/9/1.
 */
public class FileMapper {
    public List searchFile(FileEntity fileEntity) throws Exception {
        String sql=null;

       sql="select *  from manage_script" +
               "where " +
               "$if{ and fileId=#{fileId} }"+
               "$if{ and fileName LIKE  concat(#{fileName},'%') }  ";

       sql= ModelSQLUtils.dynamicSQL(sql,fileEntity);
       System.out.println(sql);
        List list=JDBCUtils.selectToMapList(sql);
        return list;
    }

    public Integer insertFileData(FileEntity fileEntity) throws SQLException {

        try {
            return JDBCUtils.insertTable(fileEntity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }

    }



    public List<Object> getTableData(HashMap map) throws NoSuchFieldException, IllegalAccessException, SQLException {
        String sql=null;

        sql="select *from #{tableName} where 1=1";

        sql=ModelSQLUtils.dynamicSQL(sql,map);
        List list=JDBCUtils.selectToMapList(sql);
        return list;
    }
}

