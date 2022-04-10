package com.zg.admin.dao;

import com.zg.admin.entity.Power;
import com.zg.database.util.BaseDao;
import com.zg.database.util.ModelSQLUtils;

import java.util.List;

public class PowerDao extends BaseDao {

    public List getPowerList(Power power) throws Exception {
        String sql="select *from cn_power where 1=1  and pid=#{pid} ";
        sql= ModelSQLUtils.dynamicSQL(sql,power);
        List list= select(sql, Power.class);
        return list;

    }
}
