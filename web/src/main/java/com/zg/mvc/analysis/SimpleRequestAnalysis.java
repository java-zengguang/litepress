package com.zg.mvc.analysis;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.mvc.annotation.controller.ParamEntity;
import com.zg.mvc.annotation.controller.RequestBody;
import org.apache.commons.collections.map.HashedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

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
