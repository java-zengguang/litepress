package com.zg.util.test;

import com.zg.database.util.JDBCUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/3/7 0007.
 */
public class TestDB
{
    public static void main(String args[]) throws SQLException, IllegalAccessException {
        List list=new ArrayList();
        Test test=new Test();
        test.id=1;
        test.name="dfs";
        test.time=new Date();
        list.add(test);
        JDBCUtils.insertTables(list,Test.class,"test");
        JDBCUtils.commit();
    }
}
