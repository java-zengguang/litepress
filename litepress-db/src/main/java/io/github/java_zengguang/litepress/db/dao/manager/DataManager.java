package io.github.java_zengguang.litepress.db.dao.manager;

import io.github.java_zengguang.litepress.db.po.EntityDataPo;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;

import java.util.List;
import java.util.Map;

public interface DataManager {

    List<Map<String, Object>> selectToMapList(String sql) throws Exception;

    List<MetaDataPo> select2TempleList(String sql, String... tableNames) throws Exception;

    List<Integer> insertTables(List<EntityDataPo> models, String tableName) throws Exception;

    Integer insertEntity(EntityDataPo model, String tableName) throws Exception;

    /**
     * 插入并返回自增主键；无自增主键或失败时返回 null。
     * 基于 JDBC {@link java.sql.Statement#RETURN_GENERATED_KEYS}，避免 {@code select @@IDENTITY} 的触发器干扰与跨库方言问题。
     */
    Long insertEntityReturnKey(EntityDataPo model, String tableName) throws Exception;

    Integer updateModel(EntityDataPo model, String... terms) throws Exception;

    Integer operation(String sql) throws Exception;

    List<String> selectOneColList(String sql) throws Exception;
}
