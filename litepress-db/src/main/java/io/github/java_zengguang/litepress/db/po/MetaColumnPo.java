package io.github.java_zengguang.litepress.db.po;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

import java.util.List;

public class MetaColumnPo extends MainModel {

    public String columnLabel;
    public String columnType;
    public Integer columnScale;
    public String columnValue;
    public Object jdbcValue;

/*    public String fieldName;
    public String fieldType;
    public Object fieldValue;*/


    public String isNotCommit;  //1-提交  0-不提交
    public String isAutoIncrease;  //0-不自增  1-自增
    //主键
    public String isPK;      //0-不是主键  1-是主键



}
