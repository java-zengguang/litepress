package com.zg.bean.entity;

import java.util.Date;

/**
 * Created by Administrator on 2018/11/29 0029.
 */
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
