package com.zg.admin.entity;

import com.zg.bean.annotation.AutoIncrease;
import com.zg.bean.annotation.Model;
import com.zg.bean.annotation.NotCommitField;
import com.zg.bean.entity.MainModel;

import java.util.List;

@Model(tableName = "cn_menu")
public class MenuInfo extends MainModel {
    @AutoIncrease
    @NotCommitField
    public int id;
    public int pid;
    public String text;
    public String  status;
    public int level;
    public String url;
    public String icon;
    @NotCommitField
    public List children;

    @Override
    public String toString() {
        return "MenuInfo{" +
                "id=" + id +
                ", pid=" + pid +
                ", text='" + text + '\'' +
                ", status='" + status + '\'' +
                ", level=" + level +
                ", url='" + url + '\'' +
                ", icon='" + icon + '\'' +
                ", children=" + children +
                '}';
    }
}
