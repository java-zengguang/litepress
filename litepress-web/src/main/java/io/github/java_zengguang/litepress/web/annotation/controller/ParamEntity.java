package io.github.java_zengguang.litepress.web.annotation.controller;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

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
