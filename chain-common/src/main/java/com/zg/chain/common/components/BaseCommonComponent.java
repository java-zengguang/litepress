package com.zg.chain.common.components;


import com.zg.chain.common.entity.BaseProcess;

public abstract class BaseCommonComponent extends BaseComponent {
/*
    public Map<String, Object> beanMap = new HashMap<>();  //设计用于节点间非标数据交换，用于接受上一节点传入信息，和保存下一节点传出信息
    public String systemFlag;
    public String functionFlag;*/


    public BaseProcess doExecuteProcess(BaseProcess baseProcess) throws Exception {
        BaseProcess synEntity = doExecute(baseProcess);
        return synEntity;
    }


    public abstract BaseProcess doExecute(BaseProcess baseProcess) throws Exception;

}
