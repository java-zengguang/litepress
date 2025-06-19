package com.zg.mvc.intercept;

import com.zg.common.init.Config;
import com.zg.mvc.entity.MVCOption;
import com.zg.mvc.util.ThreadLocalCache;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

import java.io.IOException;

public class RenewalPostIntercept implements PostControllerIntercept {
    private static final MVCOption mvcOption = (MVCOption) Config.getConfig("MVCOption");


    @Override
    public Object doInvoke(HttpServletRequest request, HttpServletResponse response, Object args) throws IOException {
        if ("1".equals(mvcOption.powerLevel)) {
            String token = (String) ThreadLocalCache.getCache("token");
            if (token != null && !"".equals(token)) {
                Cookie newCookie = new Cookie("token", token); //假如要删除名称为username的Cookie JSESSIONID是cookie名 记得换成要删除的
                newCookie.setPath("/");
                response.addCookie(newCookie); //重新写入，将覆盖之前的
                Logger.info("重新写入token:" + token);
            }
        }
        return args;
    }
}
