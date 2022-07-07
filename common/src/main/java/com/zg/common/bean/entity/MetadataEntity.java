package com.zg.common.bean.entity;

public class MetadataEntity extends MainModel {
    public String tableName;
    public String entityName;
    public String columnLabel;
    public String columnType;
    public String fieldName;
    public String fieldType;
    public Object objectValue;
    public Object fieldValue;
    public String columnValue;
    public String isCommit;  //0-提交  1-不提交
}
