package com.zg.webdemo.service.table;


import com.zg.common.dao.util.MongoDBUtils;
import com.zg.webdemo.dao.TableMapper;
import com.zg.webdemo.entity.PageEntity;
import com.zg.webdemo.entity.Table;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengguang on 2018/8/31.
 */
public class TableServiceImpl implements TableService {
    public TableMapper tableMapper;

    public Integer deleteTableDate(Map map) {

        if (null != map.get("tableName") && !("").equals(map.get("tableName")) && null != map.get("id") && !("").equals(map.get("id"))) {
            String idStr = (String) map.get("id");
            String[] ids = idStr.split(",");
            try {
                for (String id : ids) {
                    map.put("id", id);
                    if (tableMapper.deleteTableDate(map) < 0) {
                        return 0;
                    }
                }
            } catch (SQLException e) {
                Logger.error(e);

            } catch (IllegalAccessException e) {
                Logger.error(e);
            } catch (ClassNotFoundException e) {
                Logger.error(e);
            } catch (NoSuchFieldException e) {
                Logger.error(e);
            }
        } else {
            Logger.info("参数错误");
            return 0;
        }
        return 1;
    }    private final Logger Logger = Logger.getLogger(this.getClass().getName());

    public List searchTableName(Table table) {
        List list = new ArrayList();
        try {
            list = tableMapper.searchTableName(table);
        } catch (Exception e) {
            Logger.error(e);
        }
        return list;
    }

    public List<Object> getTableDate(Table table) {
        HashMap map = new HashMap();
        map.put("tableName", table.getTableName());
        map.put("createBy", table.getCreateBy());
        map.put("updateBy", table.getUpdateBy());
        map.put("createDate", table.getCreatDate());
        map.put("updateDate", table.getUpdateDate());
        List list = new ArrayList();
        try {
            list = MongoDBUtils.getDocumentList(table.getTableName(), new HashedMap());
            if (list == null || list.size() == 0) {
                list = tableMapper.getTableData(map);
                if (list.size() > 0) {
                    MongoDBUtils.deleteDocument(table.getTableName(), new HashedMap());
                    MongoDBUtils.insertMapList(table.getTableName(), list);
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        Logger.info("缓存取出" + list);
        return list;
    }

    public List<Object> getTableDataPage(Table table, PageEntity page) {

        HashMap map = new HashMap();
        map.put("tableName", table.getTableName());
        map.put("createBy", table.getCreateBy());
        map.put("updateBy", table.getUpdateBy());
        map.put("createDate", table.getCreatDate());
        map.put("updateDate", table.getUpdateDate());
        List list = new ArrayList();

        try {
            list = tableMapper.getTableDataPage(map, page);
        } catch (Exception e) {
            Logger.error(e);
        }

        return list;


    }





}
