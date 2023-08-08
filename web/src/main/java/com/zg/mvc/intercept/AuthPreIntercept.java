package com.zg.mvc.intercept;

import com.zg.common.init.Config;
import com.zg.mvc.auth.AuthManager;
import com.zg.mvc.entity.MVCOption;
import com.zg.mvc.util.ThreadLocalCache;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

import java.util.Map;

public class AuthPreIntercept implements PreControllerIntercept{
    private static MVCOption mvcOption = (MVCOption) Config.getConfig("MVCOption");

    private static AuthManager authManager = new AuthManager();

    //权限校验
    private boolean isPower(HttpServletRequest request) {
        try {
            if (authManager.isWhite(request)) {
                return true;
            }
            String token = request.getHeader("token");
            if (token == null || "".equals(token)) {
                Cookie[] cookies = request.getCookies();
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                }
            }
            if (token != null) {
                Map userInfo = authManager.verify(token);
                if (userInfo != null) {
                    Logger.info("验签通过" + userInfo);
                    token= authManager.renewalToken(token);
                    ThreadLocalCache.setCache("currentUserInfo", userInfo);
                    ThreadLocalCache.setCache("token", token);
                    return true;
                }
            }

        } catch (Exception e) {
            Logger.info(e.getMessage());
            return false;
        }
        return false;
    }

    @Override
    public boolean doInvoke(HttpServletRequest request, HttpServletResponse response) {
        if ("1".equals(mvcOption.powerLevel) && !isPower(request)) {
            response.setStatus(401);
            Cookie newCookie = new Cookie("token", ""); //假如要删除名称为username的Cookie JSESSIONID是cookie名 记得换成要删除的
            newCookie.setMaxAge(0); //立即删除型
            newCookie.setPath("/");
            response.addCookie(newCookie); //重新写入，将覆盖之前的
            return false;
        }
        return true;
    }
}
