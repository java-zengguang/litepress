package com.zg.webdemo.entity;

import com.zg.bean.annotation.FieldTypeMode;
import com.zg.bean.annotation.Model;
import com.zg.bean.entity.MainModel;

@Model(tableName = "tablerelationship")
@FieldTypeMode(typeMode = "entity")
public class TableRelationShipEntity extends MainModel {
    public String tablename;
    public String basetablename;
    public String  databasename;
    public String prefix;
    public String suffix;
    public String type;

    public TableRelationShipEntity() {
    }

    public TableRelationShipEntity(String tablename, String basetablename, String databasename, String prefix, String suffix, String type) {
        this.tablename = tablename;
        this.basetablename = basetablename;
        this.databasename = databasename;
        this.prefix = prefix;
        this.suffix = suffix;
        this.type = type;
    }
}
