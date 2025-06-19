package com.zg.mvc.annotation.controller;

import com.zg.common.bean.entity.MainModel;

import java.lang.annotation.Annotation;

public class ParamEntity extends MainModel {
    public String paramName;
    public Object paramObject;
    public Class paramType;

    public boolean isJson;
    public Annotation[] annotations;
}
