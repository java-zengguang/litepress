package io.github.java_zengguang.litepress.chain.components;

import io.github.java_zengguang.litepress.chain.entity.BaseProcess;
import io.github.java_zengguang.litepress.chain.entity.BaseProcessBatch;
import org.tinylog.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseBatchComponent implements Component {

    public Map<String, Object> componentBeanMap = new ConcurrentHashMap<>();  //设计用于节点间非标数据交换，用于接受上一节点传入信息，和保存下一节点传出信息

    public BaseProcessBatch<BaseProcess> doExecuteBatchProcess(BaseProcessBatch baseProcessBatch) {

        if ("0".equals(baseProcessBatch.status)) {
            baseProcessBatch.status = "3";
        }
        if ("3".equals(baseProcessBatch.status)) {
            try {
                baseProcessBatch = doExecuteBatch(baseProcessBatch);
            } catch (Exception e) {
                Logger.error(e);
                baseProcessBatch.status = "-1";
                baseProcessBatch.setMessage(e.getMessage());
                List<BaseProcess> executeList = baseProcessBatch.executeList;
                for (BaseProcess baseProcess : executeList) {
                    baseProcess.executestate = "-1";
                    baseProcess.setErrormassage(e.getMessage());
                }

            }
            if ("-1".equals(baseProcessBatch.status)) {
                baseProcessBatch.errorList.addAll(baseProcessBatch.executeList);
                baseProcessBatch.executeList.clear();
            }
        }

        return baseProcessBatch;
    }

    //批量接口处理
    public abstract BaseProcessBatch<BaseProcess> doExecuteBatch(BaseProcessBatch baseProcessBatch) throws Exception;    //单个接口处理，留给子类实现


}
