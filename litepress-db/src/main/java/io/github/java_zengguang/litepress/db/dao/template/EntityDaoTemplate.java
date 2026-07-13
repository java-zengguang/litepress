package io.github.java_zengguang.litepress.db.dao.template;

import io.github.java_zengguang.litepress.db.po.EntityDataPo;
import io.github.java_zengguang.litepress.db.po.EntityFieldPo;
import io.github.java_zengguang.litepress.db.po.MetaColumnPo;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;

import java.lang.reflect.Field;

public interface EntityDaoTemplate {
    EntityFieldPo translateEntity(MetaColumnPo metadataEntity);

    EntityDataPo translateEntity(MetaDataPo metadataEntity);


    //基于entityFieldPo 构建MetaColumnPo ，用于反向转换为默认的MetaColumnPo类型，一般不用
    MetaColumnPo translateDatabase(EntityFieldPo entityFieldPo);

    Object translateObject(Field field, Object jdbcValue);

}
