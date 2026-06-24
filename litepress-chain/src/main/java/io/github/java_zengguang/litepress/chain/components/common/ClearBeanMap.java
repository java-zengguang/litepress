package io.github.java_zengguang.litepress.chain.components.common;

import io.github.java_zengguang.litepress.chain.components.BaseCommonComponent;
import io.github.java_zengguang.litepress.chain.components.Components;
import io.github.java_zengguang.litepress.chain.entity.BaseProcess;

@Components(name = "ClearBeanMap", type = "java")

public class ClearBeanMap extends BaseCommonComponent {


    @Override
    public BaseProcess doExecute(BaseProcess baseProcess) throws Exception {
        baseProcess.beanMap.clear();
        return baseProcess;
    }
}
