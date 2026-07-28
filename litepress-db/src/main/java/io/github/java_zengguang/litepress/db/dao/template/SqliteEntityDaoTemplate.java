package io.github.java_zengguang.litepress.db.dao.template;

import io.github.java_zengguang.litepress.db.po.EntityFieldPo;
import io.github.java_zengguang.litepress.db.po.MetaColumnPo;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqliteEntityDaoTemplate extends BaseEntityDaoTemplate {
    // DB列类型 → [Java类型名, SQL前缀, SQL后缀]
    // SQLite是动态类型数据库，列类型按声明类型识别，这里覆盖SQLite常见类型及其亲和类型
    private Map<String, List<String>> columnConfigMap;
    // Java类型名 → [SQL前缀, SQL后缀]
    private Map<String, List<String>> fieldConfigMap;

    public SqliteEntityDaoTemplate() {
        init();
    }

    private void init() {
        columnConfigMap = new HashMap<>();
        // 整数亲和
        columnConfigMap.put("INTEGER", List.of("Integer", "", ""));
        columnConfigMap.put("INT", List.of("Integer", "", ""));
        columnConfigMap.put("INT2", List.of("Integer", "", ""));
        columnConfigMap.put("INT8", List.of("Long", "", ""));
        columnConfigMap.put("BIGINT", List.of("Long", "", ""));
        columnConfigMap.put("SMALLINT", List.of("Integer", "", ""));
        columnConfigMap.put("TINYINT", List.of("Integer", "", ""));
        columnConfigMap.put("MEDIUMINT", List.of("Integer", "", ""));
        columnConfigMap.put("BOOLEAN", List.of("Boolean", "", ""));
        // 实数亲和
        columnConfigMap.put("REAL", List.of("Double", "", ""));
        columnConfigMap.put("DOUBLE", List.of("Double", "", ""));
        columnConfigMap.put("DOUBLE PRECISION", List.of("Double", "", ""));
        columnConfigMap.put("FLOAT", List.of("Double", "", ""));
        columnConfigMap.put("DECFLOAT", List.of("Double", "", ""));
        columnConfigMap.put("NUMERIC", List.of("BigDecimal", "", ""));
        columnConfigMap.put("DECIMAL", List.of("BigDecimal", "", ""));
        // 文本亲和
        columnConfigMap.put("VARCHAR", List.of("String", "'", "'"));
        columnConfigMap.put("VARCHAR2", List.of("String", "'", "'"));
        columnConfigMap.put("NVARCHAR", List.of("String", "'", "'"));
        columnConfigMap.put("NVARCHAR2", List.of("String", "'", "'"));
        columnConfigMap.put("CHARACTER VARYING", List.of("String", "'", "'"));
        columnConfigMap.put("CHARACTER", List.of("String", "'", "'"));
        columnConfigMap.put("CHAR", List.of("String", "'", "'"));
        columnConfigMap.put("NCHAR", List.of("String", "'", "'"));
        columnConfigMap.put("TEXT", List.of("String", "'", "'"));
        columnConfigMap.put("CLOB", List.of("String", "'", "'"));
        columnConfigMap.put("JSON", List.of("String", "'", "'"));
        // 日期时间（SQLite本身无日期类型，通常以TEXT字符串存储）
        columnConfigMap.put("DATE", List.of("LocalDate", "'", "'"));
        columnConfigMap.put("DATETIME", List.of("LocalDateTime", "'", "'"));
        columnConfigMap.put("TIMESTAMP", List.of("LocalDateTime", "'", "'"));

        fieldConfigMap = new HashMap<>();
        fieldConfigMap.put("Long", List.of("", ""));
        fieldConfigMap.put("BigDecimal", List.of("", ""));
        fieldConfigMap.put("Integer", List.of("", ""));
        fieldConfigMap.put("Double", List.of("", ""));
        fieldConfigMap.put("Boolean", List.of("", ""));
        fieldConfigMap.put("String", List.of("'", "'"));
        fieldConfigMap.put("LocalDate", List.of("'", "'"));
        fieldConfigMap.put("LocalDateTime", List.of("'", "'"));
        fieldConfigMap.put("Date", List.of("'", "'")); // 兼容旧的java.util.Date
    }

    @Override
    public EntityFieldPo translateEntity(MetaColumnPo metadataEntity) {
        EntityFieldPo entityFieldPo = new EntityFieldPo();
        entityFieldPo.fieldName = metadataEntity.columnLabel;
        entityFieldPo.fieldValue = metadataEntity.jdbcValue;

        List<String> configList = columnConfigMap.get(metadataEntity.columnType);
        if (configList == null || configList.isEmpty()) {
            Logger.info("错误的类型" + metadataEntity.columnType);
            return null;
        }
        entityFieldPo.fieldType = configList.get(0);
        return entityFieldPo;
    }


    @Override
    public MetaColumnPo translateDatabase(EntityFieldPo entityFieldPo) {
        MetaColumnPo metaColumnPo = new MetaColumnPo();
        metaColumnPo.columnLabel = entityFieldPo.fieldName;
        List<String> configList = fieldConfigMap.get(entityFieldPo.fieldType);
        if (entityFieldPo.fieldValue != null) {
            if ("String".equals(entityFieldPo.fieldType)) {
                String value = (String) metaColumnPo.jdbcValue;
                if (value.contains("'")) {
                    value = value.replace("'", "''");
                    metaColumnPo.jdbcValue = value;
                }
            }

            if ("BigDecimal".equals(entityFieldPo.fieldType) && metaColumnPo.jdbcValue instanceof BigDecimal) { //直接使用BigDecimal会出现尾部0丢失的情况，所以用String转一下
                BigDecimal bigDecimal = (BigDecimal) metaColumnPo.jdbcValue;
                metaColumnPo.columnValue = bigDecimal.toString();
            }
            if (configList != null && !configList.isEmpty()) {

                if ("LocalDate".equals(entityFieldPo.fieldType)) {
                    metaColumnPo.columnValue = configList.get(0) + metaColumnPo.jdbcValue + configList.get(1);
                } else if ("LocalDateTime".equals(entityFieldPo.fieldType)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    metaColumnPo.columnValue = configList.get(0) + formatter.format((LocalDateTime) metaColumnPo.jdbcValue) + configList.get(1);
                } else if ("Date".equals(entityFieldPo.fieldType)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime ldt = new Timestamp(((Date) metaColumnPo.jdbcValue).getTime()).toLocalDateTime();
                    metaColumnPo.columnValue = configList.get(0) + formatter.format(ldt) + configList.get(1);
                } else {
                    metaColumnPo.columnValue = configList.get(0) + metaColumnPo.jdbcValue + configList.get(1);
                }
            }
        }

        return metaColumnPo;
    }


}
