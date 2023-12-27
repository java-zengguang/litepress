package com.zg.common.bean.entity;

public class ColumnInfo {

    public String columnName;
    public String columnType;

    public String isPK;  //0-非主键  1-主键


/*    YES --- if the column can include NULLs
    NO --- if the column cannot include NULLs
    empty string --- if the nullability for the column is unknown*/

    public String isNullAble;

    public String columnSize;//COLUMN_SIZE 字段长度

    public String decimalDigits; //DECIMAL_DIGITS 数字精度
    public String columnLine;  //用于创建表的字段行
}
