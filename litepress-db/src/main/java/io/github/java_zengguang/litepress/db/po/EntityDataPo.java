package io.github.java_zengguang.litepress.db.po;


import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

import java.util.List;

public class EntityDataPo extends MainModel {
    public String entityName;
    public String tableName;
    public List<String> pkColumnList;
    public List<EntityFieldPo> entityFieldPos;
}
