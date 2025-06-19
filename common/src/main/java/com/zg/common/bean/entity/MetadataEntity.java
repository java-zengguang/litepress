package com.zg.common.bean.entity;

import java.util.List;

public class MetadataEntity extends MainModel {
    public String dbType;

    public String ownName; //属主 可以填属性

    public String tableName;
    public String entityName;
    public String columnLabel;
    public String columnType;

    public Integer columnScale;

    public String fieldName;
    public String fieldType;
    public Object objectValue;
    public Object fieldValue;
    public String columnValue;
    public String isNotCommit;  //1-提交  0-不提交

    public String isAutoIncrease;  //0-不自增  1-自增
    //主键
    public String isPK;      //0-不是主键  1-是主键

    public List<String> pkColumnList;


}
