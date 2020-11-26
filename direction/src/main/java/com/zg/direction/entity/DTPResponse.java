package com.zg.direction.entity;

import com.zg.bean.entity.MainModel;
import net.sf.json.JSONObject;

public class DTPResponse extends MainModel {
    public boolean success;
    public String error;
    public String resultData;
    public String resultType;


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


    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
