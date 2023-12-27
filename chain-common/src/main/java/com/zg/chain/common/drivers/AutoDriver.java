package com.zg.chain.common.drivers;


import com.zg.chain.common.entity.BaseProcess;
import com.zg.chain.common.entity.BaseProcessBatch;

import java.util.List;
import java.util.Map;

public interface AutoDriver {
    List<String> getCalssLine();

    Map<String, Map<String, Object>> getLineInitParam();

    <T extends BaseProcessBatch, t extends BaseProcess> T doExecute(T baseProcessBatch) throws Exception;
}
