package com.zg.direction.entity;

import com.zg.common.bean.entity.MainModel;

import java.util.List;

public class DTPRequest extends MainModel {
    public String id;

    public String uuid;

    public String token;

    public String className;

    public String methodName;

    public String resultType;

    public String resultDataType;

    public List<Object> methodParamters;

    public List<String> methodParamterTypes;

    public List<String> methodParamterDataTypes;

    public DTPRequest() {
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List<Object> getMethodParamters() {
        return methodParamters;
    }

    public void setMethodParamters(List<Object> methodParamters) {
        this.methodParamters = methodParamters;
    }

    public List<String> getMethodParamterTypes() {
        return methodParamterTypes;
    }

    public void setMethodParamterTypes(List<String> methodParamterTypes) {
        this.methodParamterTypes = methodParamterTypes;
    }

    public String getResultDataType() {
        return resultDataType;
    }

    public void setResultDataType(String resultDataType) {
        this.resultDataType = resultDataType;
    }

    public List<String> getMethodParamterDataTypes() {
        return methodParamterDataTypes;
    }

    public void setMethodParamterDataTypes(List<String> methodParamterDataTypes) {
        this.methodParamterDataTypes = methodParamterDataTypes;
    }
}
