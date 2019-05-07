package com.zg.admin.entity;


import java.io.Serializable;

public class Attributes implements Cloneable, Serializable {
    public String url;
    public String icon;

    @Override
    public String toString() {
        return "Attributes{" +
                "url='" + url + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
