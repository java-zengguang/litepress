package com.zg.chain.common.components;


import com.zg.chain.common.entity.BaseProcess;

public abstract class BaseCommonComponent extends BaseComponent {


    public BaseProcess doExecuteProcess(BaseProcess baseProcess) throws Exception {
        return doExecute(baseProcess);
    }


    public abstract BaseProcess doExecute(BaseProcess baseProcess) throws Exception;

}
