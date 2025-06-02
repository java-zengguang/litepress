package com.zg.litepress.db.dao.template;

import com.zg.litepress.core.bean.entity.MetadataEntity;

public interface EntityDaoTemplate {
    MetadataEntity translateEntity(MetadataEntity metadataEntity);

    MetadataEntity translateDatabase(MetadataEntity metadataEntity);

}
