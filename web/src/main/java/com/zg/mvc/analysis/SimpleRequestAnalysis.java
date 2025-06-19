package com.zg.mvc.analysis;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.mvc.annotation.controller.ParamEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class SimpleRequestAnalysis extends BaseRequestAnalysis {


    @Override
    public Object extractParam(ParamEntity paramEntity) {
        Object obj = null;
        try {
            if (paramEntity.isJson) {

                String value = "";
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader((InputStream) paramEntity.paramObject, "utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                value = sb.toString();
                obj = JsonUtils.jsonToObject(value, paramEntity.paramType);
                return obj;
            }
            if (HttpServletRequest.class.isAssignableFrom(paramEntity.paramType)) {
                return paramEntity.paramObject;
            }
            if (HttpServletResponse.class.isAssignableFrom(paramEntity.paramType)) {
                return paramEntity.paramObject;
            }
            if (EntityUtils.isPrimitive(paramEntity.paramType)) {
                obj = EntityUtils.translateType((String) paramEntity.paramObject, paramEntity.paramType);
                return obj;
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.info("解析参数失败："+e.getMessage());

        }
        return null;
    }
}
