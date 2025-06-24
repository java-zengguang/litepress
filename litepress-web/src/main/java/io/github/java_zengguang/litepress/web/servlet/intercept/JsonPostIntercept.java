package io.github.java_zengguang.litepress.web.servlet.intercept;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;
import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class JsonPostIntercept implements PostControllerIntercept {
    @Override
    public Object doInvoke(HttpServletRequest request, HttpServletResponse response, Object args) throws IOException {
        if (args != null) {
            if (args instanceof MainModel || args instanceof Collection<?> || args instanceof Map<?,?>) {
                response.setHeader("content-type", "application/json");
                response.setCharacterEncoding("UTF-8");
                return JsonUtil.obj2String(args);
            }
        }
        return args;
    }
}
