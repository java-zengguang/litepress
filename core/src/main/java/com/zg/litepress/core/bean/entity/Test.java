package com.zg.litepress.core.bean.entity;

import com.zg.litepress.core.annotation.Model;

import java.util.Date;

/**
 * Created by Administrator on 2018/11/29 0029.
 */
@Model
public class Test extends MainModel {
    public int id;
    public String name;
    public Date time;

    public Test() {
    }

    public Test(int id, String name, Date time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }
}
