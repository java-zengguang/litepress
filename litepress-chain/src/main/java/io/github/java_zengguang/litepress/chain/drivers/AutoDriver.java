package io.github.java_zengguang.litepress.chain.drivers;


import io.github.java_zengguang.litepress.chain.entity.BaseProcess;
import io.github.java_zengguang.litepress.chain.entity.BaseProcessBatch;

import java.util.List;
import java.util.Map;

public interface AutoDriver {
    List<String> getCalssLine();

    Map<String, Map<String, Object>> getLineInitParam();

    <T extends BaseProcessBatch, t extends BaseProcess> T doExecute(T baseProcessBatch) throws Exception;
     BaseProcess doExecute(BaseProcess baseProcess) throws Exception;
}
