package com.zg.common.dao.template;

import com.zg.common.bean.entity.MetadataEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class OracleEntityDaoTemplate extends BaseEntityDaoTemplate {
    private List<List<String>> configArrayList;
    private Map<String, List<String>> columnConfigMap;  //第一列放fieldtype,第二三列放引号用来拼sql
    private Map<String, List<String>> fieldConfigMap;

    public OracleEntityDaoTemplate() {
        init();
    }

    private void init() {

        configArrayList =new ArrayList<List<String>>(){
            {
                add( Arrays.asList("INTEGER","Integer", "", ""));
                add(Arrays.asList("INT", "Integer", "", ""));
                add(Arrays.asList("DECFLOAT", "Double", "", ""));
                add( Arrays.asList("VARCHAR","String", "'", "'"));
                add(Arrays.asList("VARCHAR2", "String", "'", "'"));
                add( Arrays.asList("NVARCHAR2","String", "'", "'"));
                add( Arrays.asList("CHARACTER VARYING","String", "'", "'"));
                add( Arrays.asList("CHAR","String", "'", "'"));
                add( Arrays.asList("TEXT","String", "'", "'"));
                add( Arrays.asList("NUMBER","BigDecimal", "", ""));
                add( Arrays.asList("NUMERIC","BigDecimal", "", ""));
                add( Arrays.asList("DATE","Date", "to_date('", "','yyyy-MM-dd)"));
                add( Arrays.asList("DATETIME","Date", "to_date('", "','yyyy-MM-dd hh24:mi:ss')"));
                add( Arrays.asList("TIMESTAMP","Date", "to_date('", "','yyyy-MM-dd hh24:mi:ss')"));}

        };

        columnConfigMap = new HashMap();
        for(List<String> list: configArrayList){
            columnConfigMap.put(list.get(0),list);
        }

        fieldConfigMap = new HashMap();
        for(List<String> list: configArrayList){
            fieldConfigMap.put(list.get(1),list);
        }
    }

    @Override
    public MetadataEntity translateEntity(MetadataEntity metadataEntity) {
        metadataEntity.entityName = metadataEntity.tableName;
        metadataEntity.fieldName = metadataEntity.columnLabel;
        metadataEntity.fieldValue = metadataEntity.objectValue;
        List<String> configList = columnConfigMap.get(metadataEntity.columnType);
        metadataEntity.fieldType = configList.get(1);
        if (metadataEntity.objectValue != null) {
            if("BigDecimal".equals(metadataEntity.fieldType)){ //直接使用BigDecimal会出现尾部0丢失的情况，所以用String转一下
                metadataEntity.fieldValue= BigDecimal.valueOf(Double.parseDouble(""+metadataEntity.objectValue));
            }
            if ("Date".equals(metadataEntity.fieldType)) {
                if(metadataEntity.objectValue instanceof Timestamp) {
                    metadataEntity.fieldValue = new Date(((Timestamp) metadataEntity.objectValue).getTime()); //将timestap转成date，防止比较对不上
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                metadataEntity.columnValue = configList.get(2) + dateFormat.format(metadataEntity.objectValue) + configList.get(3);
            } else {
                metadataEntity.columnValue = configList.get(2) + String.valueOf(metadataEntity.objectValue) + configList.get(3);
            }
        }
        return metadataEntity;
    }

    @Override
    public MetadataEntity translateDatabase(MetadataEntity metadataEntity) {

        metadataEntity.tableName = metadataEntity.entityName;
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

            if (configList!=null&&configList.size()>0) {
                if ("Date".equals(metadataEntity.fieldType)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    metadataEntity.columnValue = configList.get(2) + dateFormat.format(metadataEntity.objectValue) + configList.get(3);
                } else {
                    metadataEntity.columnValue = configList.get(2) + String.valueOf(metadataEntity.objectValue) + configList.get(3);
                }
            }
        }

        return metadataEntity;
    }


}
