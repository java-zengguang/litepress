package com.zg.webdemo.util;


import com.zg.common.dao.util.NewJDBCUtil;
import com.zg.webdemo.entity.PageEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by zkyd01 on 2018/9/3.
 */
public class TableUtil {


    public static String addPageFromSql(String sql, PageEntity page) throws Exception {
        String countSql = null;
        if (sql != null) {
            countSql = "select count(1) as totalResultSize  from ( " + sql + " ) as num";
        }
        NewJDBCUtil jdbcUtil = new NewJDBCUtil("optionDB");
        List list = jdbcUtil.selectToMapList(countSql);
        jdbcUtil.release();
        Map map = (Map) list.get(0);
        Integer totalResultSize = Integer.valueOf((String) map.get("totalResultSize"));
        page.setTotalResultSize(totalResultSize);
        page.setTotalPageSize(totalResultSize / page.getPageSize());
        Integer startRows = (page.getCurrentPage() - 1) * page.getPageSize();
        /*  Integer endRows=(page.getCurrentPage())*page.getPageSize();*/
        sql = sql + " limit " + startRows + " , " + page.getPageSize();
        return sql;
    }


}
