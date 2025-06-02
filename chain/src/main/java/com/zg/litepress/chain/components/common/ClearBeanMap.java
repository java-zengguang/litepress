package com.zg.litepress.chain.components.common;

import com.zg.litepress.chain.components.BaseCommonComponent;
import com.zg.litepress.chain.components.Components;
import com.zg.litepress.chain.entity.BaseProcess;

@Components(name = "ClearBeanMap", type = "java")

public class ClearBeanMap extends BaseCommonComponent {


    @Override
    public BaseProcess doExecute(BaseProcess baseProcess) throws Exception {
        baseProcess.beanMap.clear();
        return baseProcess;
    }
}
