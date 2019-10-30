package com.zg.direction.entity;

import com.zg.bean.entity.MainModel;
import net.sf.json.JSONObject;

public class DTPResponse extends MainModel {
    public boolean success;
    public String error;
    public JSONObject resultData;
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

    public JSONObject getResultData() {
        return resultData;
    }



    public void setResultData(JSONObject resultData) {
        this.resultData = resultData;
    }

    public void setResultData(String resultData) {
        this.resultData= JSONObject.fromObject(resultData);
    }


    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
