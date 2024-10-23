package com.zg.mvc.analysis;


import com.zg.common.util.reflect.JsonUtil;
import com.zg.common.util.reflect.TransEntityTypeUtils;
import com.zg.mvc.annotation.controller.ParamEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class SimpleRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        Object obj = null;
        try {
            if (paramEntity.isJson) {

                String value = "";
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader((InputStream) paramEntity.paramObject, StandardCharsets.UTF_8));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                value = sb.toString();
                obj = JsonUtil.string2Obj(value, (Class<?>) paramEntity.paramGenericityType);
                return obj;
            }
            if (HttpServletRequest.class.isAssignableFrom(paramEntity.paramType)) {
                return paramEntity.paramObject;
            }
            if (HttpServletResponse.class.isAssignableFrom(paramEntity.paramType)) {
                return paramEntity.paramObject;
            }
            if (TransEntityTypeUtils.isPrimitive(paramEntity.paramType)) {
                obj = TransEntityTypeUtils.translateType((String) paramEntity.paramObject, paramEntity.paramType);
                return obj;
            }
        } catch (Exception e) {
            Logger.error(e);
            Logger.info("解析参数失败：" + e.getMessage());

        }
        return null;
    }
}
