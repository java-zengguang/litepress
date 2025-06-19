package com.zg.common.bean.entity;

import java.util.List;

public class TableInfo extends MainModel {
    public String tableName;
    public List<ColumnInfo> columnList;

    public List<ColumnInfo> pkColumnList;
}
