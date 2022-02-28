package com.zg.admin.dao;

import com.zg.admin.entity.Power;
import com.zg.database.util.JDBCUtils;
import com.zg.database.util.ModelSQLUtils;

import java.util.List;

public class PowerDao {

    public List getPowerList(Power power) throws Exception {
        String sql="select *from cn_power where 1=1  and pid=#{pid} ";
        sql= ModelSQLUtils.dynamicSQL(sql,power);
        List list=JDBCUtils.select(sql, Power.class);
        return list;

    }
}
