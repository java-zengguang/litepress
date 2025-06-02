package com.zg.litepress.chain.components;


import com.zg.litepress.chain.entity.BaseProcess;
import com.zg.litepress.chain.entity.BaseProcessBatch;

public interface Component<T extends BaseProcessBatch<S>, S extends BaseProcess> {
    BaseProcessBatch<BaseProcess> doExecuteBatchProcess(BaseProcessBatch baseProcessBatch);
}
