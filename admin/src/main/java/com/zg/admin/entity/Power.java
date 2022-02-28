package com.zg.admin.entity;

import com.zg.bean.annotation.Model;
import com.zg.bean.entity.MainModel;

@Model(tableName = "cn_power")
public class Power extends MainModel {
    public int id;
    public int pid;
    public String name;
    public String icon;
    public String url;
    public int rank;
    public boolean status;

}
