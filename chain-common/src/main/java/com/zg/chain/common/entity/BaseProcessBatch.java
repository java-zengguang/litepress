package com.zg.chain.common.entity;

import com.zg.common.annotation.NotCommitField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseProcessBatch<T extends BaseProcess> {
    @NotCommitField
    public String status = "0";
    @NotCommitField
    public String systemflag; //系统标识  new-non-auto 非车新一代   old-non-auto  老非车  platform  平台
    @NotCommitField
    public String functionflag;  //  execute 执行   check 检查  rollback 回滚
    @NotCommitField
    public String executebatchno;
    @NotCommitField
    public String message;
    @NotCommitField
    public Integer batchThreadNum = 0;//线程数  默认不开多线程

    @NotCommitField
    public List<T> executeList = new ArrayList<>();
    @NotCommitField
    public List<T> errorList = new ArrayList<>();


    @NotCommitField
    public Map<String, Object> batchBeanMap = new ConcurrentHashMap<>();//设计用于批量流程间非标数据交换，用于接受上一节点传入信息，和保存下一节点传出信息


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message != null) {

            message = message.replaceAll("'", "");
            if (message.length() > 200) {
                message = message.substring(0, 200);
            }
            if (this.message == null) {
                this.message = "";
            }
            this.message = this.message + ";" + message;
        }
    }


}
