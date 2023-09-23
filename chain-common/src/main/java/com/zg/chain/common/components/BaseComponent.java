package com.zg.chain.common.components;

import com.zg.chain.common.entity.BaseProcess;
import com.zg.chain.common.entity.BaseProcessBatch;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseComponent implements Component {

    public Map<String, Object> componentBeanMap = new ConcurrentHashMap<>();  //设计用于节点间非标数据交换，用于接受上一节点传入信息，和保存下一节点传出信息


    public BaseProcessBatch<BaseProcess> doExecuteBatchProcess(BaseProcessBatch baseProcessBatch) {
        if ("0".equals(baseProcessBatch.status)) {
            baseProcessBatch.status = "3";
        }
        if ("3".equals(baseProcessBatch.status)) {
            List<BaseProcess> baseProcesses = baseProcessBatch.executeList;
            List<BaseProcess> errorList = new ArrayList<>();
            for (BaseProcess baseProcess : baseProcesses) {
                if ("0".equals(baseProcess.executestate)) {
                    baseProcess.executestate = "3";
                }
                if ("3".equals(baseProcess.executestate)) {
                    try {

                        baseProcess = doExecuteProcess(baseProcess);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.error(e);
                        baseProcess.executestate = "-1";
                        baseProcess.setErrormassage(this.getClass() + ";" + e.getMessage());
                        errorList.add(baseProcess);
                        continue;
                    }
                }
            }
            if (errorList.size() > 0) {
                baseProcessBatch.executeList.removeAll(errorList);
                baseProcessBatch.errorList.addAll(errorList);
                baseProcessBatch.status = "-1";
            }
        }

        return baseProcessBatch;
    }

    //批量接口处理


    //单个接口处理，留给子类实现
    public abstract BaseProcess doExecuteProcess(BaseProcess baseProcess) throws Exception;


}
