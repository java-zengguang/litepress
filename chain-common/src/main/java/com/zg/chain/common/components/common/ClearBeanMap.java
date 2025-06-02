package com.zg.chain.common.components.common;

import com.zg.chain.common.components.BaseCommonComponent;
import com.zg.chain.common.components.Components;
import com.zg.chain.common.entity.BaseProcess;

@Components(name = "ClearBeanMap", type = "java")

public class ClearBeanMap extends BaseCommonComponent {


    @Override
    public BaseProcess doExecute(BaseProcess baseProcess) throws Exception {
        baseProcess.beanMap.clear();
        return baseProcess;
    }
}
