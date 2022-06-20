package com.zg.direction.entity;

import com.zg.common.bean.entity.MainModel;

public class DTPResponse extends MainModel {
    public String id;
    public boolean success;
    public String error;
    public Object resultData;   //返回值
    public String resultType;  //返回值类型
    public String resultDataType;  //返回值数据类型


    public DTPResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }



    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getResultDataType() {
        return resultDataType;
    }

    public void setResultDataType(String resultDataType) {
        this.resultDataType = resultDataType;
    }
}
