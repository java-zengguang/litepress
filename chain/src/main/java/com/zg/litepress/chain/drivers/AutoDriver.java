package com.zg.litepress.chain.drivers;


import com.zg.litepress.chain.entity.BaseProcess;
import com.zg.litepress.chain.entity.BaseProcessBatch;

import java.util.List;
import java.util.Map;

public interface AutoDriver {
    List<String> getCalssLine();

    Map<String, Map<String, Object>> getLineInitParam();

    <T extends BaseProcessBatch, t extends BaseProcess> T doExecute(T baseProcessBatch) throws Exception;
     BaseProcess doExecute(BaseProcess baseProcess) throws Exception;
}
