package com.zg.mvc.analysis;

import com.alibaba.fastjson.JSON;
import com.zg.common.util.reflect.JsonUtil;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.mvc.annotation.controller.ParamEntity;
import com.zg.mvc.annotation.controller.RequestBody;

import java.lang.annotation.Annotation;

public class JsonRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        String data=(String) paramEntity.paramObject;
        Object obj= JSON.parseObject(data,paramEntity.paramGenericityType);
        return obj;
    }
}
