package com.zg.webdemo.service.table;


import com.zg.webdemo.entity.PageEntity;
import com.zg.webdemo.entity.Table;

import java.util.List;
import java.util.Map;

/**
 * Created by zkyd01 on 2018/8/31.
 */


public interface TableService {

    List searchTableName(Table table);

    Integer deleteTableDate(Map map);


    List<Object> getTableDate(Table table);

    List<Object> getTableDataPage(Table table, PageEntity page);

}
