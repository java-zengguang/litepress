package io.github.java_zengguang.litepress.db.dao.template;

import io.github.java_zengguang.litepress.core.bean.entity.MetaColumnPo;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlEntityDaoTemplate extends BaseEntityDaoTemplate {
    // DB列类型 → [Java类型名, SQL前缀, SQL后缀]
    private Map<String, List<String>> columnConfigMap;
    // Java类型名 → [SQL前缀, SQL后缀]
    private Map<String, List<String>> fieldConfigMap;

    public MysqlEntityDaoTemplate() {
        init();
    }

    private void init() {
        columnConfigMap = new HashMap<>();
        columnConfigMap.put("BIGINT", List.of("Long", "", ""));
        columnConfigMap.put("DECIMAL", List.of("BigDecimal", "", ""));
        columnConfigMap.put("DOUBLE", List.of("BigDecimal", "", ""));
        columnConfigMap.put("INTEGER", List.of("Integer", "", ""));
        columnConfigMap.put("INT", List.of("Integer", "", ""));
        columnConfigMap.put("DECFLOAT", List.of("Double", "", ""));
        columnConfigMap.put("VARCHAR", List.of("String", "'", "'"));
        columnConfigMap.put("VARCHAR2", List.of("String", "'", "'"));
        columnConfigMap.put("NVARCHAR2", List.of("String", "'", "'"));
        columnConfigMap.put("CHARACTER VARYING", List.of("String", "'", "'"));
        columnConfigMap.put("CHAR", List.of("String", "'", "'"));
        columnConfigMap.put("TEXT", List.of("String", "'", "'"));
        columnConfigMap.put("JSON", List.of("String", "'", "'"));
        columnConfigMap.put("NUMBER", List.of("BigDecimal", "", ""));
        columnConfigMap.put("NUMERIC", List.of("BigDecimal", "", ""));
        columnConfigMap.put("DATE", List.of("LocalDate", "'", "'"));
        columnConfigMap.put("DATETIME", List.of("LocalDateTime", "'", "'"));
        columnConfigMap.put("TIMESTAMP", List.of("LocalDateTime", "'", "'"));
        columnConfigMap.put("MEDIUMTEXT", List.of("String", "'", "'"));

        fieldConfigMap = new HashMap<>();
        fieldConfigMap.put("Long", List.of("", ""));
        fieldConfigMap.put("BigDecimal", List.of("", ""));
        fieldConfigMap.put("Integer", List.of("", ""));
        fieldConfigMap.put("Double", List.of("", ""));
        fieldConfigMap.put("String", List.of("'", "'"));
        fieldConfigMap.put("LocalDate", List.of("'", "'"));
        fieldConfigMap.put("LocalDateTime", List.of("'", "'"));
        fieldConfigMap.put("Date", List.of("'", "'")); // 兼容旧的java.util.Date
    }

    @Override
    public MetaColumnPo translateEntity(MetaColumnPo metadataEntity) {
        metadataEntity.fieldName = metadataEntity.columnLabel;
        metadataEntity.fieldValue = metadataEntity.jdbcValue;

        List<String> configList = columnConfigMap.get(metadataEntity.columnType);
        if (configList == null || configList.isEmpty()) {
            Logger.info("错误的类型" + metadataEntity.columnType);
            return null;
        }
        metadataEntity.fieldType = configList.get(0);


        return metadataEntity;
    }

    @Override
    public MetaColumnPo translateDatabase(MetaColumnPo metadataEntity) {

        //   metadataEntity.tableName = metadataEntity.entityName;
        metadataEntity.columnLabel = metadataEntity.fieldName;
        List<String> configList = fieldConfigMap.get(metadataEntity.fieldType);
        if (metadataEntity.jdbcValue != null) {

            if ("String".equals(metadataEntity.fieldType)) {
                String value = (String) metadataEntity.jdbcValue;
                if (value.contains("'")) {
                    value = value.replace("'", "''");
                    metadataEntity.jdbcValue = value;
                }
            }

            if ("BigDecimal".equals(metadataEntity.fieldType) && metadataEntity.jdbcValue instanceof BigDecimal) { //直接使用BigDecimal会出现尾部0丢失的情况，所以用String转一下
                BigDecimal bigDecimal = (BigDecimal) metadataEntity.jdbcValue;
                metadataEntity.columnValue = bigDecimal.toString();
            }
            if (configList != null && !configList.isEmpty()) {

                if ("LocalDate".equals(metadataEntity.fieldType)) {
                    metadataEntity.columnValue = configList.get(0) + metadataEntity.jdbcValue + configList.get(1);
                } else if ("LocalDateTime".equals(metadataEntity.fieldType)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    metadataEntity.columnValue = configList.get(0) + formatter.format((LocalDateTime) metadataEntity.jdbcValue) + configList.get(1);
                } else if ("Date".equals(metadataEntity.fieldType)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime ldt = new Timestamp(((Date) metadataEntity.jdbcValue).getTime()).toLocalDateTime();
                    metadataEntity.columnValue = configList.get(0) + formatter.format(ldt) + configList.get(1);
                } else {
                    metadataEntity.columnValue = configList.get(0) + metadataEntity.jdbcValue + configList.get(1);
                }
            }
        }

        return metadataEntity;
    }


}