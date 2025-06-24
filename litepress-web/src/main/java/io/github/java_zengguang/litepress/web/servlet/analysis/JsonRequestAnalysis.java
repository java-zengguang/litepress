package io.github.java_zengguang.litepress.web.servlet.analysis;


import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.web.annotation.controller.ParamEntity;

public class JsonRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        String data = (String) paramEntity.paramObject;
        return  JsonUtil.string2Obj(data,(Class<?>)paramEntity.paramGenericityType );

    }
}
