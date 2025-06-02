package com.zg.litepress.chain.components;


import com.zg.litepress.chain.entity.BaseProcess;

public abstract class BaseCommonComponent extends BaseComponent {


    public BaseProcess doExecuteProcess(BaseProcess baseProcess) throws Exception {
        return doExecute(baseProcess);
    }


    public abstract BaseProcess doExecute(BaseProcess baseProcess) throws Exception;

}
