package com.zg.admin.entity;

import com.zg.bean.annotation.Model;
import com.zg.bean.entity.MainModel;

/**
 * Created by Administrator on 2019/3/14 0014.
 */
@Model(tableName = "cn_role")
public class Role extends MainModel{
    public int id;
    public String name;
    public String powers;
    public String introduce;
    public boolean status;

}
