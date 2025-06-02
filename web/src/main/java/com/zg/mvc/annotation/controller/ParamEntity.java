package com.zg.mvc.annotation.controller;

import com.zg.common.bean.entity.MainModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class ParamEntity extends MainModel {
    public String paramName;
    public Object paramObject;
    public Class paramType;

    public Type paramGenericityType;

    public boolean isJson;
    public Annotation[] annotations;
}
