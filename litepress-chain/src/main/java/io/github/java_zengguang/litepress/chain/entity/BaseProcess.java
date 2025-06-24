package io.github.java_zengguang.litepress.chain.entity;

import io.github.java_zengguang.litepress.core.annotation.NotCommitField;
import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseProcess extends MainModel {

    public String systemflag; //系统标识  new-non-auto 非车新一代   old-non-auto  老非车  platform  平台
    public String functionflag;  //  execute 执行   check 检查  rollback 回滚
    public String executestate = "0";
    public String executebatchno;
    public String errormassage;


    @NotCommitField
    public Map<String, Object> beanMap = new ConcurrentHashMap<>();//设计用于节点间非标数据交换，用于接受上一节点传入信息，和保存下一节点传出信息
    @NotCommitField
    public Integer repeatCont = 0;

    public String getErrormassage() {
        return errormassage;
    }

    public void setErrormassage(String errormassage) {
        if (errormassage != null) {
            String message = errormassage.replaceAll("'", "");
            if (message.length() > 200) {
                message = message.substring(0, 200);
            }
            if (this.errormassage == null) {
                this.errormassage = "";
            }
            this.errormassage = this.errormassage + ";" + message;
        }
    }

    public String getSystemflag() {
        return systemflag;
    }

    public void setSystemflag(String systemflag) {
        this.systemflag = systemflag;
    }

    public String getFunctionflag() {
        return functionflag;
    }

    public void setFunctionflag(String functionflag) {
        this.functionflag = functionflag;
    }

    public String getExecutestate() {
        return executestate;
    }

    public void setExecutestate(String executestate) {
        this.executestate = executestate;
    }

    public String getExecutebatchno() {
        return executebatchno;
    }

    public void setExecutebatchno(String executebatchno) {
        this.executebatchno = executebatchno;
    }
}
