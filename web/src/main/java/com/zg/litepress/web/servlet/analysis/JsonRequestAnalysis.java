package com.zg.litepress.web.servlet.analysis;


import com.zg.litepress.core.util.reflect.JsonUtil;
import com.zg.litepress.web.annotation.controller.ParamEntity;

public class JsonRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        String data = (String) paramEntity.paramObject;
        return  JsonUtil.string2Obj(data,(Class<?>)paramEntity.paramGenericityType );

    }
}
