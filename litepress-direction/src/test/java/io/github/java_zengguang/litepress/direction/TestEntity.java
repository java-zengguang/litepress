package io.github.java_zengguang.litepress.direction;


import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

import java.util.List;

public class TestEntity extends MainModel {
    public int x;
    public String s;
    public List<String> list;

    public TestEntity() {
    }

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

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "x=" + x +
                ", s='" + s + '\'' +
                '}';
    }
}
