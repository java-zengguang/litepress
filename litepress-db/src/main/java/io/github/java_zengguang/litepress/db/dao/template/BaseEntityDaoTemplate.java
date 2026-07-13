package io.github.java_zengguang.litepress.db.dao.template;

import io.github.java_zengguang.litepress.db.po.EntityDataPo;
import io.github.java_zengguang.litepress.db.po.MetaColumnPo;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;
import org.tinylog.Logger;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public abstract class BaseEntityDaoTemplate implements EntityDaoTemplate {

    @Override
    public EntityDataPo translateEntity(MetaDataPo metadataEntity) {
        EntityDataPo entityDataPo = new EntityDataPo();
        entityDataPo.entityName = metadataEntity.tableName;
        entityDataPo.tableName = metadataEntity.tableName;
        entityDataPo.pkColumnList = metadataEntity.pkColumnList;
        List<MetaColumnPo> metaColumnPos = metadataEntity.columnPos;
        entityDataPo.entityFieldPos = metaColumnPos.stream().map(this::translateEntity).toList();
        return entityDataPo;
    }

    @Override
    public Object translateObject(Field field, Object jdbcValue) {
        if (jdbcValue == null) {
            return null;
        }
        String entityFieldType = field.getType().getSimpleName();
        String jdbcFieldType = jdbcValue.getClass().getSimpleName();

        // 基本类型与包装类统一处理
        entityFieldType = wrapPrimitive(entityFieldType);

        // 类型一致，直接返回
        if (entityFieldType.equals(jdbcFieldType)) {
            return jdbcValue;
        }

        switch (jdbcFieldType) {
            case "Timestamp":
                Timestamp ts = (Timestamp) jdbcValue;
                switch (entityFieldType) {
                    case "LocalDateTime": return ts.toLocalDateTime();
                    case "LocalDate": return ts.toLocalDateTime().toLocalDate();
                    case "LocalTime": return ts.toLocalDateTime().toLocalTime();
                    case "Date": return new Date(ts.getTime());
                    case "String": return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                break;
            // java.sql.Date 和 java.util.Date 的 getSimpleName 都是 "Date"，需要 instanceof 区分
            case "Date":
                if (jdbcValue instanceof java.sql.Date) {
                    java.sql.Date sqlDate = (java.sql.Date) jdbcValue;
                    switch (entityFieldType) {
                        case "LocalDate": return sqlDate.toLocalDate();
                        case "LocalDateTime": return sqlDate.toLocalDate().atStartOfDay();
                        case "String": return sqlDate.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                } else {
                    Date utilDate = (Date) jdbcValue;
                    LocalDateTime ldt = new Timestamp(utilDate.getTime()).toLocalDateTime();
                    switch (entityFieldType) {
                        case "LocalDateTime": return ldt;
                        case "LocalDate": return ldt.toLocalDate();
                        case "LocalTime": return ldt.toLocalTime();
                        case "String": return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                }
                break;
            case "Time":
                Time sqlTime = (Time) jdbcValue;
                switch (entityFieldType) {
                    case "LocalTime": return sqlTime.toLocalTime();
                    case "LocalDateTime": return LocalDate.now().atTime(sqlTime.toLocalTime());
                    case "Date": return new Date(sqlTime.getTime());
                    case "String": return sqlTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                }
                break;
            case "LocalDateTime":
                LocalDateTime ldt = (LocalDateTime) jdbcValue;
                switch (entityFieldType) {
                    case "Date": return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                    case "LocalDate": return ldt.toLocalDate();
                    case "LocalTime": return ldt.toLocalTime();
                    case "String": return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                break;
            case "LocalDate":
                LocalDate ld = (LocalDate) jdbcValue;
                switch (entityFieldType) {
                    case "Date": return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    case "LocalDateTime": return ld.atStartOfDay();
                    case "String": return ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                break;
            case "LocalTime":
                LocalTime lt = (LocalTime) jdbcValue;
                switch (entityFieldType) {
                    case "Date": return Date.from(LocalDate.now().atTime(lt).atZone(ZoneId.systemDefault()).toInstant());
                    case "LocalDateTime": return LocalDate.now().atTime(lt);
                    case "String": return lt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                }
                break;
            case "BigDecimal":
                BigDecimal bigDecimal = (BigDecimal) jdbcValue;
                switch (entityFieldType) {
                    case "Long": return bigDecimal.longValue();
                    case "Integer": return bigDecimal.intValue();
                    case "Short": return bigDecimal.shortValue();
                    case "Byte": return bigDecimal.byteValue();
                    case "Double": return bigDecimal.doubleValue();
                    case "Float": return bigDecimal.floatValue();
                    case "Boolean": return bigDecimal.intValue() != 0;
                    case "String": return bigDecimal.toString();
                    case "BigInteger": return bigDecimal.toBigInteger();
                }
                break;
            case "BigInteger":
                BigInteger bigInt = (BigInteger) jdbcValue;
                switch (entityFieldType) {
                    case "Long": return bigInt.longValue();
                    case "Integer": return bigInt.intValue();
                    case "Short": return bigInt.shortValue();
                    case "Byte": return bigInt.byteValue();
                    case "BigDecimal": return new BigDecimal(bigInt);
                    case "Boolean": return bigInt.intValue() != 0;
                    case "String": return bigInt.toString();
                }
                break;
            case "Long":
                Long longValue = (Long) jdbcValue;
                switch (entityFieldType) {
                    case "Integer": return longValue.intValue();
                    case "Short": return longValue.shortValue();
                    case "Byte": return longValue.byteValue();
                    case "BigDecimal": return BigDecimal.valueOf(longValue);
                    case "Double": return longValue.doubleValue();
                    case "Float": return longValue.floatValue();
                    case "Boolean": return longValue != 0;
                    case "String": return longValue.toString();
                }
                break;
            case "Integer":
                Integer intValue = (Integer) jdbcValue;
                switch (entityFieldType) {
                    case "Long": return intValue.longValue();
                    case "Short": return intValue.shortValue();
                    case "Byte": return intValue.byteValue();
                    case "BigDecimal": return BigDecimal.valueOf(intValue);
                    case "Double": return intValue.doubleValue();
                    case "Float": return intValue.floatValue();
                    case "Boolean": return intValue != 0;
                    case "String": return intValue.toString();
                }
                break;
            case "Short":
                Short shortValue = (Short) jdbcValue;
                switch (entityFieldType) {
                    case "Long": return shortValue.longValue();
                    case "Integer": return shortValue.intValue();
                    case "Byte": return shortValue.byteValue();
                    case "BigDecimal": return BigDecimal.valueOf(shortValue);
                    case "Double": return shortValue.doubleValue();
                    case "Float": return shortValue.floatValue();
                    case "Boolean": return shortValue != 0;
                    case "String": return shortValue.toString();
                }
                break;
            case "Byte":
                Byte byteValue = (Byte) jdbcValue;
                switch (entityFieldType) {
                    case "Long": return byteValue.longValue();
                    case "Integer": return byteValue.intValue();
                    case "Short": return byteValue.shortValue();
                    case "BigDecimal": return BigDecimal.valueOf(byteValue);
                    case "Double": return byteValue.doubleValue();
                    case "Float": return byteValue.floatValue();
                    case "Boolean": return byteValue != 0;
                    case "String": return byteValue.toString();
                }
                break;
            case "Double":
                Double doubleValue = (Double) jdbcValue;
                switch (entityFieldType) {
                    case "Float": return doubleValue.floatValue();
                    case "Long": return doubleValue.longValue();
                    case "Integer": return doubleValue.intValue();
                    case "Short": return doubleValue.shortValue();
                    case "Byte": return doubleValue.byteValue();
                    case "BigDecimal": return BigDecimal.valueOf(doubleValue);
                    case "Boolean": return doubleValue != 0.0;
                    case "String": return doubleValue.toString();
                }
                break;
            case "Float":
                Float floatValue = (Float) jdbcValue;
                switch (entityFieldType) {
                    case "Double": return floatValue.doubleValue();
                    case "Long": return floatValue.longValue();
                    case "Integer": return floatValue.intValue();
                    case "Short": return floatValue.shortValue();
                    case "Byte": return floatValue.byteValue();
                    case "BigDecimal": return BigDecimal.valueOf(floatValue);
                    case "Boolean": return floatValue != 0.0f;
                    case "String": return floatValue.toString();
                }
                break;
            case "Boolean":
                Boolean boolValue = (Boolean) jdbcValue;
                switch (entityFieldType) {
                    case "Long": return boolValue ? 1L : 0L;
                    case "Integer": return boolValue ? 1 : 0;
                    case "Short": return boolValue ? (short) 1 : (short) 0;
                    case "Byte": return boolValue ? (byte) 1 : (byte) 0;
                    case "String": return boolValue.toString();
                }
                break;
            case "String":
                String strValue = (String) jdbcValue;
                switch (entityFieldType) {
                    case "Long": return Long.parseLong(strValue);
                    case "Integer": return Integer.parseInt(strValue);
                    case "Short": return Short.parseShort(strValue);
                    case "Byte": return Byte.parseByte(strValue);
                    case "Double": return Double.parseDouble(strValue);
                    case "BigDecimal": return new BigDecimal(strValue);
                    case "Float": return Float.parseFloat(strValue);
                    case "Boolean": return Boolean.parseBoolean(strValue);
                }
                break;
        }

        Logger.info("无法映射类型: 实体类=" + entityFieldType + ", JDBC=" + jdbcFieldType);
        return null;
    }

    private String wrapPrimitive(String typeName) {
        switch (typeName) {
            case "boolean": return "Boolean";
            case "long": return "Long";
            case "int": return "Integer";
            case "short": return "Short";
            case "byte": return "Byte";
            case "double": return "Double";
            case "float": return "Float";
            default: return typeName;
        }
    }
}
