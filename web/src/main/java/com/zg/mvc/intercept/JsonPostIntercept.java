package com.zg.mvc.intercept;

import com.zg.common.bean.entity.MainModel;
import com.zg.common.util.reflect.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JsonPostIntercept implements PostControllerIntercept {
    @Override
    public Object doInvoke(HttpServletRequest request, HttpServletResponse response, Object args) throws IOException {

        if (args instanceof MainModel) {
            if (args != null) {
                response.setHeader("content-type", "application/json");
                response.setCharacterEncoding("UTF-8");
                return JsonUtils.objectToJsonString(args);
            }
        }
        return args;
    }
}
