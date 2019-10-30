package com.zg.direction.entity;

import com.zg.bean.entity.MainModel;

public class DTPRequest extends MainModel {

    public String uuid;

    public String token;

    public String className;

    public String methodName;

    public String methodType;

    public String methodParamters;

    public String methodParamterTypes;

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

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getMethodParamters() {
        return methodParamters;
    }

    public void setMethodParamters(String methodParamters) {
        this.methodParamters = methodParamters;
    }

    public String getMethodParamterTypes() {
        return methodParamterTypes;
    }

    public void setMethodParamterTypes(String methodParamterTypes) {
        this.methodParamterTypes = methodParamterTypes;
    }
}
