package io.github.java_zengguang.litepress.db.po;


import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

import java.util.List;

public class MetaDataPo extends MainModel {
    public String ownName; //属主 可以填属性
    public String tableName;
  //  public String entityName;
    public List<String> pkColumnList;
    public List<MetaColumnPo> columnPos;
}
