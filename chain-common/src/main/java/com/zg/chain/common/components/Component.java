package com.zg.chain.common.components;


import com.zg.chain.common.entity.BaseProcess;
import com.zg.chain.common.entity.BaseProcessBatch;

public interface Component<T extends BaseProcessBatch<S>, S extends BaseProcess> {
    BaseProcessBatch<BaseProcess> doExecuteBatchProcess(BaseProcessBatch baseProcessBatch);
}
