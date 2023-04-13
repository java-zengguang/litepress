package com.zg.mvc.analysis;

import com.zg.common.util.reflect.JsonUtils;
import com.zg.mvc.annotation.controller.ParamEntity;
import com.zg.mvc.annotation.controller.RequestBody;

import java.lang.annotation.Annotation;

public class JsonRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        Object obj = null;
        Annotation[] annotations = paramEntity.annotations;
        if (annotations != null && annotations.length >= 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequestBody) {
                    obj = JsonUtils.jsonToObject((String) paramEntity.paramObject, paramEntity.paramType);
                }
            }
        }
        return obj;
    }
}
