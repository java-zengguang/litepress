package com.zg.common.dao.template;

import com.zg.common.bean.entity.MetadataEntity;

public interface EntityDaoTemplate {
    MetadataEntity translateEntity(MetadataEntity metadataEntity);

    MetadataEntity translateDatabase(MetadataEntity metadataEntity);

}
