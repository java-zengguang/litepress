package com.zg.mvc.util;

import com.zg.common.util.reflect.EntityUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2018/12/12 0012.
 */
public class ExtractionParameter {

    public static Object getObject(HttpServletRequest request, Object model) throws IllegalAccessException {
        Field[] fields = model.getClass().getFields();
        for (Field field : fields) {
            String value = request.getParameter(field.getName());
            if (value != null) {
                EntityUtils.setField(field, model, value);
            }
        }

        return model;

    }
}
