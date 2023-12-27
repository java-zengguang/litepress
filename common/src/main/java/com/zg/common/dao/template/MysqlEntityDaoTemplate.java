package com.zg.common.dao.template;

import com.zg.common.bean.entity.MetadataEntity;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class MysqlEntityDaoTemplate extends BaseEntityDaoTemplate {
    private List<List<String>> configArrayList;
    private Map<String, List<String>> columnConfigMap;  //第一列放fieldtype,第二三列放引号用来拼sql
    private Map<String, List<String>> fieldConfigMap;

    public MysqlEntityDaoTemplate() {
        init();
    }

    private void init() {

        configArrayList = new ArrayList<List<String>>() {

            {
                add(Arrays.asList("DECIMAL", "BigDecimal", "", ""));
                add(Arrays.asList("DOUBLE", "BigDecimal", "", ""));
                add(Arrays.asList("INTEGER", "Integer", "", ""));
                add(Arrays.asList("INT", "Integer", "", ""));
                add(Arrays.asList("DECFLOAT", "Double", "", ""));
                add(Arrays.asList("VARCHAR", "String", "'", "'"));
                add(Arrays.asList("VARCHAR2", "String", "'", "'"));
                add(Arrays.asList("NVARCHAR2", "String", "'", "'"));
                add(Arrays.asList("CHARACTER VARYING", "String", "'", "'"));
                add(Arrays.asList("CHAR", "String", "'", "'"));
                add(Arrays.asList("TEXT", "String", "'", "'"));
                add(Arrays.asList("JSON", "String", "'", "'"));
                add(Arrays.asList("NUMBER", "BigDecimal", "", ""));
                add(Arrays.asList("NUMERIC", "BigDecimal", "", ""));
                add(Arrays.asList("DATE", "Date", "to_date('", "','yyyy-MM-dd)"));
                add(Arrays.asList("DATETIME", "Date", "'", "'"));
                add(Arrays.asList("TIMESTAMP", "Date", "'", "'"));
                add(Arrays.asList("MEDIUMTEXT", "String", "'", "'"));
            }

        };

        columnConfigMap = new HashMap();
        for (List<String> list : configArrayList) {
            columnConfigMap.put(list.get(0), list);
        }

        fieldConfigMap = new HashMap();
        for (List<String> list : configArrayList) {
            fieldConfigMap.put(list.get(1), list);
        }
    }

    @Override
    public MetadataEntity translateEntity(MetadataEntity metadataEntity) {
        metadataEntity.entityName = metadataEntity.tableName.replace(".", "");
        metadataEntity.fieldName = metadataEntity.columnLabel;
        metadataEntity.fieldValue = metadataEntity.objectValue;

        List<String> configList = columnConfigMap.get(metadataEntity.columnType);
        if (configList == null || configList.size() == 0) {
            Logger.info("错误的类型" + metadataEntity.columnType);
            return null;
        }
        metadataEntity.fieldType = configList.get(1);
        if (metadataEntity.objectValue != null) {
            if ("BigDecimal".equals(metadataEntity.fieldType)) { //直接使用BigDecimal会出现尾部0丢失的情况，所以用String转一下
                BigDecimal bigDecimal = BigDecimal.valueOf(Double.parseDouble("" + metadataEntity.objectValue));
                bigDecimal = bigDecimal.setScale(metadataEntity.columnScale, RoundingMode.HALF_UP); //指定精度，避免科学计数法
                metadataEntity.fieldValue = bigDecimal;
            }
            if ("Date".equals(metadataEntity.fieldType)) {
                if (metadataEntity.objectValue instanceof Timestamp) {
                    metadataEntity.fieldValue = new Date(((Timestamp) metadataEntity.objectValue).getTime()); //将timestap转成date，防止比较对不上
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                metadataEntity.columnValue = configList.get(2) + dateFormat.format(metadataEntity.objectValue) + configList.get(3);
            } else {
                metadataEntity.columnValue = configList.get(2) + metadataEntity.objectValue + configList.get(3);
            }
        }
        return metadataEntity;
    }

    @Override
    public MetadataEntity translateDatabase(MetadataEntity metadataEntity) {

        //   metadataEntity.tableName = metadataEntity.entityName;
        metadataEntity.columnLabel = metadataEntity.fieldName;
        List<String> configList = fieldConfigMap.get(metadataEntity.fieldType);
        // metadataEntity.columnType = configList.get(0);
        if (metadataEntity.objectValue != null) {

            if ("String".equals(metadataEntity.fieldType)) {
                String value = (String) metadataEntity.objectValue;
                if (value.contains("'")) {
                    value = value.replace("'", "''");
                    metadataEntity.objectValue = value;
                }
            }

            if ("BigDecimal".equals(metadataEntity.fieldType) && metadataEntity.objectValue instanceof BigDecimal) { //直接使用BigDecimal会出现尾部0丢失的情况，所以用String转一下
                BigDecimal bigDecimal = (BigDecimal) metadataEntity.objectValue;
                metadataEntity.columnValue = bigDecimal.toString();
            }
            if (configList != null && configList.size() > 0) {

                if ("Date".equals(metadataEntity.fieldType)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    metadataEntity.columnValue = configList.get(2) + dateFormat.format(metadataEntity.objectValue) + configList.get(3);
                } else {
                    metadataEntity.columnValue = configList.get(2) + metadataEntity.objectValue + configList.get(3);
                }
            }
        }

        return metadataEntity;
    }


}
