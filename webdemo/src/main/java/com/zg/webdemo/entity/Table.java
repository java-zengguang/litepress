package com.zg.webdemo.entity;

import com.zg.bean.entity.MainModel;

/**
 * Created by zkyd01 on 2018/9/1.
 */
public class Table extends MainModel{
    public String dateBaseName;
    public String tableName;
    public String tableType;
    public String createBy;
    public String updateBy;
    public String creatDate;
    public String updateDate;

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getCreatDate() {
        return creatDate;
    }

    public void setCreatDate(String creatDate) {
        this.creatDate = creatDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getDateBaseName() {
        return dateBaseName;
    }

    public void setDateBaseName(String dateBaseName) {
        this.dateBaseName = dateBaseName;
    }


    @Override
    public String toString() {
        return "Table{" +
                "dateBaseName='" + dateBaseName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableType='" + tableType + '\'' +
                ", createBy='" + createBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", creatDate='" + creatDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }
}
