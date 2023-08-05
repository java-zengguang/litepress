package com.zg.io.dao;


import com.zg.common.dao.util.BaseDao;
import com.zg.common.dao.util.ModelSQLUtils;
import com.zg.io.entity.FileEntity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zkyd01 on 2018/9/1.
 */
public class FileMapper extends BaseDao {
    public List searchFile(FileEntity fileEntity) throws Exception {
        String sql = null;

        sql = "select *  from manage_script" +
                "where " +
                "$if{ and fileId=#{fileId} }" +
                "$if{ and fileName LIKE  concat(#{fileName},'%') }  ";

        sql = ModelSQLUtils.dynamicSQL(sql, fileEntity);
        List list = selectToMapList(sql);
        return list;
    }

    public Integer insertFileData(FileEntity fileEntity) throws SQLException {

        try {
            return insertTable(fileEntity);
        } catch (IllegalAccessException | ClassNotFoundException e) {
            Logger.error(e);
            return -1;
        }

    }


    public List<Object> getTableData(HashMap map) throws NoSuchFieldException, IllegalAccessException, SQLException, ClassNotFoundException {
        String sql = null;

        sql = "select *from #{tableName} where 1=1";

        sql = ModelSQLUtils.dynamicSQL(sql, map);
        List list = selectToMapList(sql);
        return list;
    }
}

