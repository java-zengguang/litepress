package com.zg.direction;

import com.mysql.cj.xdevapi.JsonArray;

import java.util.List;

public class TestEntity {
    public int x;
    public List<String> list;

    public TestEntity(){}

    public TestEntity(int x) {
        this.x = x;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
