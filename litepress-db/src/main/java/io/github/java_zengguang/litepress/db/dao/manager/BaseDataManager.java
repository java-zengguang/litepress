package io.github.java_zengguang.litepress.db.dao.manager;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.core.relect.dynameic.DynameicSerializer;
import io.github.java_zengguang.litepress.core.relect.dynameic.DynamicClass;
import io.github.java_zengguang.litepress.db.dao.assemble.Assemble;
import io.github.java_zengguang.litepress.db.dao.assemble.SimpleAssemble;
import io.github.java_zengguang.litepress.db.dao.template.EntityDaoTemplate;
import io.github.java_zengguang.litepress.db.dao.template.EntityDaoTemplateFactory;
import io.github.java_zengguang.litepress.db.util.DBUtils;
import io.github.java_zengguang.litepress.db.util.ParseSQLUtils;
import org.tinylog.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class BaseDataManager implements DataManager {
    private final String dataSource;
    private final TransactionManager transactionManager;

    public BaseDataManager(String dataSource) {
        this.dataSource = dataSource;
        this.transactionManager = TransactionManager.getInstance();
    }

    private Connection getConnection() throws Exception {
        return transactionManager.getConnection(dataSource);
    }

    //查询出列明，数据对应的list集合
    public List<Map<String, Object>> selectToMapList(String sql) throws Exception {
        // 记录error级别的信息
        Logger.debug(sql);
        List<Map<String, Object>> list = new ArrayList();
        Connection conn = getConnection();
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
    public List<List<MetadataEntity>> select2TempleList(String sql, String... tableNames) throws Exception {
        Logger.debug(sql);
        List<List<MetadataEntity>> list = new ArrayList<>();
        //合并主表
        StringBuilder tableNameBuffer = new StringBuilder();
        List<String> tableNameList = Arrays.asList(tableNames);
        tableNameList.forEach(tableNameBuffer::append);

        Connection conn = this.getConnection();
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
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(optionDB.dbtype);

        int columncount = 0;
        while (rs.next()) {
            List<MetadataEntity> columnList = new ArrayList<>();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                String columnType = rsmd.getColumnTypeName(i);
                Integer columnScale = rsmd.getScale(i);
                if (columnScale == -127) {
                    columnScale = 6;
                }
                Object columnValue = rs.getObject(i);
                MetadataEntity metadataEntity = new MetadataEntity();
                metadataEntity.ownName = "";
                metadataEntity.tableName = tableNameBuffer.toString();
                metadataEntity.columnLabel = columnLabel;
                metadataEntity.columnType = columnType;
                metadataEntity.objectValue = columnValue;
                metadataEntity.columnScale = columnScale;
                metadataEntity.dbType = optionDB.dbtype;
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
                metadataEntity = entityDaoTemplate.translateEntity(metadataEntity);
                columnList.add(metadataEntity);
            }
            list.add(columnList);
        }
        pstmt.close();
        rs.close();
        return list;
    }


    public Class<?> selectStream(String sql, File tempFile) throws Exception {
        Class<?> modelClass = null;
        String tableName = ParseSQLUtils.parseSelectMainTable(sql).get(0);
        Connection conn = this.getConnection();
        //获取组件
        List<String> pkColumnList = new ArrayList<>();
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName);
        while (dmdrs.next()) {
            String pkStr = dmdrs.getString("COLUMN_NAME");
            pkColumnList.add(pkStr);
        }

        PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        pstmt.setFetchSize(10000);
        pstmt.setFetchDirection(ResultSet.FETCH_REVERSE);
        ResultSet rs;
        rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;
        Output output = new Output(new FileOutputStream(tempFile), 1024000);
        Kryo kryo = new Kryo();
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(optionDB.dbtype);
        while (rs.next()) {
            List<MetadataEntity> columnList = new ArrayList<>();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                Integer columnScale = rsmd.getScale(i);
                if (columnScale == -127) {
                    columnScale = 6;
                }
                String columnLabel = rsmd.getColumnLabel(i);
                String columnType = rsmd.getColumnTypeName(i);
                Object columnValue = rs.getObject(i);
                MetadataEntity metadataEntity = new MetadataEntity();
                metadataEntity.tableName = tableName;
                metadataEntity.columnLabel = columnLabel;
                metadataEntity.columnType = columnType;
                metadataEntity.objectValue = columnValue;
                metadataEntity.columnScale = columnScale;
                metadataEntity.dbType = optionDB.dbtype;
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
                metadataEntity = entityDaoTemplate.translateEntity(metadataEntity);
                columnList.add(metadataEntity);
            }
            SimpleAssemble<Object> simpleAssemble = new SimpleAssemble<>(optionDB.dbtype);
            if (modelClass == null) {
                modelClass = DynamicClass.getDynamicModel(columnList);
                kryo.register(modelClass, new DynameicSerializer(modelClass));
            }

            Object obj = modelClass.getDeclaredConstructor().newInstance();
            for (MetadataEntity metadataEntity : columnList) {
                obj = simpleAssemble.assembling(metadataEntity, obj);
            }
            kryo.writeObject(output, obj);
        }
        rs.close();
        pstmt.close();
        output.close();
        return modelClass;
    }


    public Class<?> selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws Exception {
        Class<?> modelClass = null;

        tableName = tableName.trim().toUpperCase();
        String ownName = "";
        if (tableName.contains(".")) {
            String[] splits = tableName.split("\\.");
            ownName = splits[0];
            tableName = splits[1];
        }
        Connection conn = this.getConnection();
        //获取组件
        List<String> pkColumnList = new ArrayList<>();
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet dmdrs = dmd.getPrimaryKeys(null, null, tableName.toUpperCase());
        while (dmdrs.next()) {
            String pkStr = dmdrs.getString("COLUMN_NAME");
            pkColumnList.add(pkStr);
        }


        PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        pstmt.setFetchSize(10000);
        pstmt.setFetchDirection(ResultSet.FETCH_REVERSE);
        ResultSet rs;
        rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columncount = 0;
        Kryo kryo = new Kryo();

        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        EntityDaoTemplate entityDaoTemplate = EntityDaoTemplateFactory.getTemplate(optionDB.dbtype);
        Long count = 0L;
        Output output = null;
        while (rs.next()) {
            if (count % fileSize == 0) {
                if (output != null) {
                    output.close();
                }
                File tempFile = new File(tempFileDir, "" + System.currentTimeMillis());
                tempFile.createNewFile();
                tempFileList.add(tempFile);
                output = new Output(new FileOutputStream(tempFile), 1024000);
            }

            List<MetadataEntity> columnList = new ArrayList<>();
            columncount = rsmd.getColumnCount();
            for (int i = 1; i < columncount + 1; i++) {
                Integer columnScale = rsmd.getScale(i);
                if (columnScale == -127) {
                    columnScale = 6;
                }
                String columnLabel = rsmd.getColumnLabel(i);
                String columnType = rsmd.getColumnTypeName(i);
                Object columnValue = rs.getObject(i);
                MetadataEntity metadataEntity = new MetadataEntity();
                metadataEntity.ownName = ownName;
                metadataEntity.tableName = tableName;
                metadataEntity.columnLabel = columnLabel;
                metadataEntity.columnType = columnType;
                metadataEntity.objectValue = columnValue;
                metadataEntity.columnScale = columnScale;
                metadataEntity.dbType = optionDB.dbtype;
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
                metadataEntity = entityDaoTemplate.translateEntity(metadataEntity);
                columnList.add(metadataEntity);

            }
            SimpleAssemble<Object> simpleAssemble = new SimpleAssemble<>(optionDB.dbtype);
            if (modelClass == null) {
                modelClass = DynamicClass.getDynamicModel(columnList);
                kryo.register(modelClass, new DynameicSerializer(modelClass));
            }


            Object obj = modelClass.getDeclaredConstructor().newInstance();
            for (MetadataEntity metadataEntity : columnList) {
                obj = simpleAssemble.assembling(metadataEntity, obj);
            }
            kryo.writeObject(output, obj);
            count++;
        }
        if (output != null) {
            output.close();
        }
        pstmt.close();
        rs.close();

        return modelClass;
    }


    //查询出列明，数据对应的list集合
    public List<List<MetadataEntity>> select2TempleList(String sql) throws Exception {
        Logger.debug(sql);
        List<String> tableNameList = ParseSQLUtils.parseSelectMainTable(sql);
        String[] array = tableNameList.toArray(new String[0]);
        return select2TempleList(sql, array);
    }

    //执行批操作
    public int[] operationAll(List<String> sqlList) throws Exception {
        int[] result = new int[sqlList.size()];
        for (int i = 0; i < sqlList.size(); i++) {
            String sql = sqlList.get(i);
            result[i] = operation(sql);
        }
        return result;
    }

    public Integer updateEntity(String dbType, Object model, String... terms) throws Exception {

        String sql;
        String condition = " ";
        for (String term : terms) {
            condition = condition + " and " + term;
        }
        String tableName = DBUtils.getTableNameFromModel(model.getClass());
        List<String> memberList = new ArrayList();
        List<Object> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        StringBuilder memberValues = new StringBuilder();
        for (MetadataEntity entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                if (entity.fieldName != null && entity.objectValue != null) {
                    memberList.add(entity.fieldName);
                    valuesList.add(entity.objectValue);
                    memberValues.append(" " + entity.fieldName + "=" + "?,");
                }

            }
        }


        memberValues.setCharAt(memberValues.length() - 1, ' ');
        sql = "update " + tableName + " set " + memberValues + "where 1=1 " + condition;


        Connection conn = this.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < valuesList.size(); i++) {
            Object value = valuesList.get(i);
            int parameterIndex = i + 1; // PreparedStatement 参数索引从 1 开始

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
                stmt.setTimestamp(parameterIndex, new java.sql.Timestamp(utilDate.getTime()));
            } else if (value instanceof BigDecimal) {
                stmt.setBigDecimal(parameterIndex, (BigDecimal) value);
            } else if (value instanceof byte[]) {
                stmt.setBytes(parameterIndex, (byte[]) value);
            } else {
                // 未知类型，可以打印警告或抛异常，或者尝试 toString() 后存为字符串
                System.err.println("Unsupported type for value at index " + i + ": " + value.getClass().getName());
                stmt.setObject(parameterIndex, value); // fallback，通用但不够精准
            }
        }
        int flag = stmt.executeUpdate();
        stmt.close();
        return flag;
    }

    public Integer insertEntity(Object model, String tableName, String dbType) throws Exception {
        List<String> memberList = new ArrayList<>();
        List<Object> valuesList = new ArrayList<>();
        StringBuilder member = new StringBuilder();
        StringBuilder values = new StringBuilder();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                if (entity.fieldName != null && entity.objectValue != null) {
                    memberList.add(entity.fieldName);
                    member.append(entity.fieldName + ",");
                    values.append("?,");
                    valuesList.add(entity.objectValue);
                }
            }
        }

        //   List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        String sql = null;
        member.deleteCharAt(member.length() - 1);
        values.deleteCharAt(values.length() - 1);
        sql = "insert into " + tableName + " (" + member + ") values (" + values + ")";

        Connection conn = this.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < valuesList.size(); i++) {
            Object value = valuesList.get(i);
            int parameterIndex = i + 1; // PreparedStatement 参数索引从 1 开始

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
                stmt.setTimestamp(parameterIndex, new java.sql.Timestamp(utilDate.getTime()));
            } else if (value instanceof BigDecimal) {
                stmt.setBigDecimal(parameterIndex, (BigDecimal) value);
            } else if (value instanceof byte[]) {
                stmt.setBytes(parameterIndex, (byte[]) value);
            } else {
                // 未知类型，可以打印警告或抛异常，或者尝试 toString() 后存为字符串
                System.err.println("Unsupported type for value at index " + i + ": " + value.getClass().getName());
                stmt.setObject(parameterIndex, value); // fallback，通用但不够精准
            }
        }

        Integer result = stmt.executeUpdate();
        stmt.close();
        return result;
    }

    //执行批操作
    public int[] batchSQL(List<String> sqlList) throws Exception {
        //清洗脚本
        Statement stmt;
        Connection conn = this.getConnection();
        stmt = conn.createStatement();
        Logger.debug("--------------start batch-----------");
        for (String sql : sqlList) {
            Logger.debug(sql);
            stmt.addBatch(sql);
        }
        int[] i = stmt.executeBatch();
        Logger.debug("--------------end batch-----------");
        stmt.close();
        return i;
    }


    public List<String> selectOneColList(String sql) throws Exception {
        List<String> list = new ArrayList<>();
        Connection conn = this.getConnection();
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


    public List<String> batchSqlFile(File file) throws IOException {
        // 装载list
        List<String> list = new ArrayList<String>();
        if (file != null && file.exists()) {
            // 读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String lineStr = "";
            while ((line = br.readLine()) != null) {
                lineStr = lineStr + line;
                Logger.debug(lineStr);
                // 判断截取点
                if (lineStr.endsWith(";")) {
                    lineStr = lineStr.replace(";", "");
                    list.add(lineStr);
                    lineStr = "";
                }
            }

        } else {
            Logger.debug("Sql文件没找到！");
        }
        return list;
    }


    public String getOneValue(String sql) throws Exception {
        String result = "";
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        Connection conn = getConnection();
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        while (rs.next()) {
            result = rs.getString(1);
        }
        return result;
    }

    //执行增删改
    public Integer operation(String sql) throws Exception {
        Logger.debug(sql);
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int x = pstmt.executeUpdate();
        pstmt.close();
        return x;
    }


}
