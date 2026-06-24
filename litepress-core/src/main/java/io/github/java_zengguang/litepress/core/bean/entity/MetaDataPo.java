package io.github.java_zengguang.litepress.core.bean.entity;


import java.util.List;

public class MetaDataPo extends MainModel {
    public String dbType;
    public String ownName; //属主 可以填属性
    public String tableName;
    public String entityName;
    public List<String> pkColumnList;
    public List<MetaColumnPo> columnPos;
}
