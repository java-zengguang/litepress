package com.zg.webdemo.entity;

import com.zg.bean.annotation.AutoIncrease;
import com.zg.bean.annotation.FieldTypeMode;
import com.zg.bean.annotation.Model;
import com.zg.bean.annotation.NotCommitField;
import com.zg.bean.entity.MainModel;

@Model(tableName = "databasetablestructure")
@FieldTypeMode(typeMode = "entity")
public class DatabaseTableStructureEntity extends MainModel {
    @AutoIncrease
    @NotCommitField
    public Long id;
    public String environment;
    public String databaseName;
    public String tableName;
    public String owner;
    public String columnId;
    public String columnName;
    public String keyType;
    public String columnType;
    public String nullAble;
    public String dataDefault;
    public String comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }



    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getNullAble() {
        return nullAble;
    }

    public void setNullAble(String nullAble) {
        this.nullAble = nullAble;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDataDefault() {
        return dataDefault;
    }

    public void setDataDefault(String dataDefault) {
        this.dataDefault = dataDefault;
    }


}
