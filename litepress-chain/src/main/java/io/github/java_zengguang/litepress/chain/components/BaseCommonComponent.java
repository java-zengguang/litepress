package io.github.java_zengguang.litepress.chain.components;


import io.github.java_zengguang.litepress.chain.entity.BaseProcess;

public abstract class BaseCommonComponent extends BaseComponent {


    public BaseProcess doExecuteProcess(BaseProcess baseProcess) throws Exception {
        return doExecute(baseProcess);
    }


    public abstract BaseProcess doExecute(BaseProcess baseProcess) throws Exception;

}
