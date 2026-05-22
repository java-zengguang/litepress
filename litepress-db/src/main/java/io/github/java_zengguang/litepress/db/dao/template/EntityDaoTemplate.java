package io.github.java_zengguang.litepress.db.dao.template;

import io.github.java_zengguang.litepress.core.bean.entity.MetaColumnPo;

import java.lang.reflect.Field;

public interface EntityDaoTemplate {
    MetaColumnPo translateEntity(MetaColumnPo metadataEntity);

    MetaColumnPo translateDatabase(MetaColumnPo metadataEntity);

    Object translateObject(Field field, Object jdbcValue);

}
