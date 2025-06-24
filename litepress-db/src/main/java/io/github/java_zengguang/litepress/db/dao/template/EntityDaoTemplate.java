package io.github.java_zengguang.litepress.db.dao.template;

import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;

public interface EntityDaoTemplate {
    MetadataEntity translateEntity(MetadataEntity metadataEntity);

    MetadataEntity translateDatabase(MetadataEntity metadataEntity);

}
