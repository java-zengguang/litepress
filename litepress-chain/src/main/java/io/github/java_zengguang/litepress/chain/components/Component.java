package io.github.java_zengguang.litepress.chain.components;


import io.github.java_zengguang.litepress.chain.entity.BaseProcess;
import io.github.java_zengguang.litepress.chain.entity.BaseProcessBatch;

public interface Component<T extends BaseProcessBatch<S>, S extends BaseProcess> {
    BaseProcessBatch<BaseProcess> doExecuteBatchProcess(BaseProcessBatch baseProcessBatch);
}
