package com.zg.mvc.analysis;

import com.zg.common.util.reflect.JsonUtil;
import com.zg.mvc.annotation.controller.ParamEntity;

public class JsonRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        String data = (String) paramEntity.paramObject;
        return  JsonUtil.string2Obj(data,(Class<?>)paramEntity.paramGenericityType );

    }
}
