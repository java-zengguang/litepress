package com.zg.mvc.analysis;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.mvc.annotation.controller.ParamEntity;


public class SimpleRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        Object obj=null;
            if(EntityUtils.isPrimitive(paramEntity.paramType)){
                obj = EntityUtils.translateType((String) paramEntity.paramObject,paramEntity.paramType);
            }

        return obj;
    }
}
