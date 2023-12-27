package com.zg.chain.common.drivers;

import com.zg.chain.common.components.Component;
import com.zg.chain.common.entity.BaseProcessBatch;
import com.zg.chain.common.factory.ComponentsFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAutoDriver implements AutoDriver {
    public ComponentsFactory componentsFactory = ComponentsFactory.newInstance();
    public String systemFlag;
    public String functionFlag;
    private final Map<String, Object> beanMap = new HashMap<>();  //保存当前流程的参数，用于传入下个节点使用
    private Map<String, Map<String, Object>> initParam;//各节点初始化参数

    public BaseAutoDriver() {//代码块初始话参数
        initParam();
    }

    private void initParam() {
        initParam = getLineInitParam();
    }


    @Override
    public BaseProcessBatch doExecute(BaseProcessBatch baseProcessBatch) throws Exception {

        List<String> linkClassesList = getCalssLine();
        for (String classeName : linkClassesList) {
            Component autoDriver = componentsFactory.getComponent(classeName, systemFlag, functionFlag, initParam.get(classeName), beanMap);
            baseProcessBatch = autoDriver.doExecuteBatchProcess(baseProcessBatch);
        }

        return baseProcessBatch;
    }

    @Override
    public Map<String, Map<String, Object>> getLineInitParam() {
        return new HashMap<>();
    }

}
