package com.zg.mvc.analysis;

import com.alibaba.fastjson.JSON;
import com.zg.mvc.annotation.controller.ParamEntity;

public class JsonRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        String data = (String) paramEntity.paramObject;
        return JSON.parseObject(data, paramEntity.paramGenericityType);
    }
}
