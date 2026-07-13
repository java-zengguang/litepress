package io.github.java_zengguang.litepress.db.dao.manager;


import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.db.po.EntityDataPo;
import io.github.java_zengguang.litepress.db.po.EntityFieldPo;
import io.github.java_zengguang.litepress.db.po.MetaColumnPo;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class BaseDataManager implements DataManager {
    private final Connection conn;


    public BaseDataManager(Connection conn) {
        this.conn = conn;
    }


    //查询出列明，数据对应的list集合
    public List<Map<String, Object>> selectToMapList(String sql) throws Exception {
        // 记录error级别的信息
        Logger.debug(sql);
        List<Map<String, Object>> list = new ArrayList();

        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;
        while (rs.next()) {
            Map<String, Object> map = new LinkedHashMap<>();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                Object columnValue = rs.getObject(i);
                map.put(columnLabel, columnValue);
            }
            list.add(map);
        }
        pstmt.close();
        rs.close();

        return list;
    }

    //查询出列明，数据对应的list集合
    public List<MetaDataPo> select2TempleList(String sql, String... tableNames) throws Exception {
        Logger.debug(sql);
        List<MetaDataPo> metaDataPos = new ArrayList<>();

        //合并主表
        StringBuilder tableNameBuffer = new StringBuilder();
        List<String> tableNameList = Arrays.asList(tableNames);
        tableNameList.forEach(tableNameBuffer::append);

        //获取组件
        List<String> pkColumnList = new ArrayList<>();
        DatabaseMetaData dmd = conn.getMetaData();
        for (String tableName : tableNameList) {
            ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName.toUpperCase());
            while (dmdrs.next()) {
                String pkStr = dmdrs.getString("COLUMN_NAME");
                pkColumnList.add(pkStr);
            }
        }
        //获取数据
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();


        int columncount = 0;
        while (rs.next()) {
            MetaDataPo metaDataPo = new MetaDataPo();
            metaDataPo.tableName = tableNameBuffer.toString();
            metaDataPo.ownName = "";
            List<MetaColumnPo> columnList = new ArrayList<>();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                String columnType = rsmd.getColumnTypeName(i);
                int columnScale = rsmd.getScale(i);
                if (columnScale == -127) {
                    columnScale = 6;
                }
                Object columnValue = rs.getObject(i);
                MetaColumnPo metadataEntity = new MetaColumnPo();
                metadataEntity.columnLabel = columnLabel;
                metadataEntity.columnType = columnType;
                metadataEntity.jdbcValue = columnValue;
                metadataEntity.columnScale = columnScale;
                if (pkColumnList.contains(columnLabel)) {
                    metadataEntity.isPK = "1";
                } else {
                    metadataEntity.isPK = "0";
                }
                if (rsmd.isAutoIncrement(i)) {
                    metadataEntity.isAutoIncrease = "1";  //自增
                    //  metadataEntity.isNotCommit="1"; //自增不提交
                } else {
                    metadataEntity.isAutoIncrease = "0";
                    metadataEntity.isNotCommit = "0";  //不自增的列才提交
                }
                columnList.add(metadataEntity);
            }
            metaDataPo.columnPos = columnList;
            metaDataPos.add(metaDataPo);
        }
        pstmt.close();
        rs.close();
        return metaDataPos;
    }


    public Integer updateModel(EntityDataPo entityDataPo, String... terms) throws Exception {

        String sql;
        String condition = " ";
        for (String term : terms) {
            condition = condition + " and " + term;
        }
        String tableName = entityDataPo.tableName;
        List<String> memberList = new ArrayList();
        List<Object> valuesList = new ArrayList();
        List<EntityFieldPo> list = entityDataPo.entityFieldPos;
        StringBuilder memberValues = new StringBuilder();
        for (EntityFieldPo entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                if (entity.fieldName != null && entity.fieldValue != null) {
                    memberList.add(entity.fieldName);
                    valuesList.add(entity.fieldValue);
                    memberValues.append(" " + entity.fieldName + "=" + "?,");
                }

            }
        }
        memberValues.setCharAt(memberValues.length() - 1, ' ');
        sql = "update " + tableName + " set " + memberValues + "where 1=1 " + condition;


        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < valuesList.size(); i++) {
            setStatementValue(stmt, i + 1, valuesList.get(i));
        }
        int flag = stmt.executeUpdate();
        stmt.close();
        return flag;
    }

    public Integer insertEntity(EntityDataPo entityDataPo, String tableName) throws Exception {
        List<String> memberList = new ArrayList<>();
        List<Object> valuesList = new ArrayList<>();
        StringBuilder member = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<EntityFieldPo> list = entityDataPo.entityFieldPos;
        for (EntityFieldPo entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                if (entity.fieldName != null && entity.fieldValue != null) {
                    memberList.add(entity.fieldName);
                    member.append(entity.fieldName + ",");
                    values.append("?,");
                    valuesList.add(entity.fieldValue);
                }
            }
        }

        //   List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        String sql = null;
        member.deleteCharAt(member.length() - 1);
        values.deleteCharAt(values.length() - 1);
        sql = "insert into " + tableName + " (" + member + ") values (" + values + ")";

        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < valuesList.size(); i++) {
            setStatementValue(stmt, i + 1, valuesList.get(i));
        }
        Integer result = stmt.executeUpdate();
        stmt.close();
        return result;
    }

    /**
     * 插入并返回自增主键。基于 {@link Statement#RETURN_GENERATED_KEYS}，
     * 返回本次 insert 语句生成的主键（用 long 承载），不受触发器/连接级 @@IDENTITY 语义干扰，跨库通用。
     */
    public Long insertEntityReturnKey(EntityDataPo entityDataPo, String tableName) throws Exception {
        List<Object> valuesList = new ArrayList<>();
        StringBuilder member = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<EntityFieldPo> list = entityDataPo.entityFieldPos;
        for (EntityFieldPo entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                if (entity.fieldName != null && entity.fieldValue != null) {
                    member.append(entity.fieldName).append(",");
                    values.append("?,");
                    valuesList.add(entity.fieldValue);
                }
            }
        }
        member.deleteCharAt(member.length() - 1);
        values.deleteCharAt(values.length() - 1);
        String sql = "insert into " + tableName + " (" + member + ") values (" + values + ")";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < valuesList.size(); i++) {
                setStatementValue(stmt, i + 1, valuesList.get(i));
            }
            stmt.executeUpdate();
            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    return gk.getLong(1);
                }
            }
        }
        return null;
    }

    /**
     * 批量插入：以第一条数据的提交列为准生成 SQL，对每条数据 addBatch 后一次性 executeBatch。
     * 要求 entityDataPos 中各对象为同一类型且提交字段一致（批量场景常规假设）。
     */
    @Override
    public List<Integer> insertTables(List<EntityDataPo> entityDataPos, String tableName) throws Exception {
        if (entityDataPos == null || entityDataPos.isEmpty()) {
            return new ArrayList<>();
        }
        // 用第一条数据确定提交的列
        List<EntityFieldPo> firstFields = entityDataPos.getFirst().entityFieldPos;
        List<String> columnList = new ArrayList<>();
        for (EntityFieldPo entity : firstFields) {
            if ("1".equals(entity.isNotCommit) && entity.fieldName != null && entity.fieldValue != null) {
                columnList.add(entity.fieldName);
            }
        }
        if (columnList.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder member = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        for (String col : columnList) {
            member.append(col).append(",");
            placeholders.append("?,");
        }
        member.deleteCharAt(member.length() - 1);
        placeholders.deleteCharAt(placeholders.length() - 1);
        String sql = "insert into " + tableName + " (" + member + ") values (" + placeholders + ")";
        Logger.debug(sql);

        PreparedStatement stmt = conn.prepareStatement(sql);
        try {
            for (EntityDataPo entityDataPo : entityDataPos) {
                List<EntityFieldPo> fields = entityDataPo.entityFieldPos;
                Map<String, Object> valueMap = new LinkedHashMap<>();
                for (EntityFieldPo entity : fields) {
                    if ("1".equals(entity.isNotCommit) && entity.fieldName != null && entity.fieldValue != null) {
                        valueMap.put(entity.fieldName, entity.fieldValue);
                    }
                }
                int parameterIndex = 1; // PreparedStatement 参数索引从 1 开始
                for (String col : columnList) {
                    setStatementValue(stmt, parameterIndex++, valueMap.get(col));
                }
                stmt.addBatch();
            }
            int[] counts = stmt.executeBatch();
            List<Integer> result = new ArrayList<>(counts.length);
            for (int count : counts) {
                result.add(count);
            }
            return result;
        } finally {
            stmt.close();
        }
    }

    // PreparedStatement 参数绑定，按 Java 类型分派
    private void setStatementValue(PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        if (value == null) {
            // 如果值为 null，需要指定字段类型，这里假设为 VARCHAR，可根据实际情况调整
            stmt.setNull(parameterIndex, Types.VARCHAR);
        } else if (value instanceof String) {
            stmt.setString(parameterIndex, (String) value);
        } else if (value instanceof Integer) {
            stmt.setInt(parameterIndex, (Integer) value);
        } else if (value instanceof Long) {
            stmt.setLong(parameterIndex, (Long) value);
        } else if (value instanceof Double) {
            stmt.setDouble(parameterIndex, (Double) value);
        } else if (value instanceof Float) {
            stmt.setFloat(parameterIndex, (Float) value);
        } else if (value instanceof Boolean) {
            stmt.setBoolean(parameterIndex, (Boolean) value);
        } else if (value instanceof java.sql.Date) {
            stmt.setDate(parameterIndex, (java.sql.Date) value);
        } else if (value instanceof java.util.Date utilDate) {
            // 如果是 java.util.Date，通常推荐转为 Timestamp
            stmt.setTimestamp(parameterIndex, new Timestamp(utilDate.getTime()));
        } else if (value instanceof BigDecimal) {
            stmt.setBigDecimal(parameterIndex, (BigDecimal) value);
        } else if (value instanceof byte[]) {
            stmt.setBytes(parameterIndex, (byte[]) value);
        } else {
            // 未知类型，可以打印警告或抛异常，或者尝试 toString() 后存为字符串
            Logger.info("Unsupported type for value at index " + parameterIndex + ": " + value.getClass().getName());
            stmt.setObject(parameterIndex, value); // fallback，通用但不够精准
        }
    }


    public List<String> selectOneColList(String sql) throws Exception {
        List<String> list = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs;
        rs = pstmt.executeQuery();
        while (rs.next()) {
            String value = rs.getString(1); // 此方法比较高效
            list.add(value);
        }
        rs.close();
        pstmt.close();
        return list;
    }


    //执行增删改
    public Integer operation(String sql) throws Exception {
        Logger.debug(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int x = pstmt.executeUpdate();
        pstmt.close();
        return x;
    }


}
