package com.zg.admin.entity;

import com.zg.bean.annotation.Model;
import com.zg.bean.entity.MainModel;

import java.util.List;

@Model(tableName = "cn_menu")
public class Menu extends MainModel {
    public int id;
    public int pid;
    public String text;
    public String  status;
    public int level;
    public Attributes attributes;
    public List children;


    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", pid=" + pid +
                ", text='" + text + '\'' +
                ", status=" + status +
                ", level=" + level +
                ", attributes=" + attributes.toString() +
                ", children=" + children +
                '}';
    }
}
