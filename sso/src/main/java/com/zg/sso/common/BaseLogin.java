package com.zg.sso.common;

import com.zg.common.init.Config;
import com.zg.sso.entity.SSOOpthion;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public abstract class BaseLogin implements LoginInte {


    public void setCookies(HttpServletResponse response, List<Cookie> cookieList, String domain, String rootPath, String url) {
        for (Cookie cookie : cookieList) {
            cookie.setDomain(domain);
            cookie.setPath(rootPath);
            cookie.setMaxAge(30 * 60);
            response.addCookie(cookie);
        }
    }

    public String getCookieValue(String key, HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void clearCookie(HttpServletResponse response, String key, String domain, String rootPath) {
        Cookie cookie = new Cookie(key, "");
        cookie.setMaxAge(0);
        cookie.setPath(domain);
        cookie.setPath(rootPath);
        response.addCookie(cookie);
    }

    public void clearCookie(HttpServletResponse response, String key) {
        SSOOpthion ssoOpthion = (SSOOpthion) Config.getConfig("SSOOpthion");
        Cookie cookie = new Cookie(key, "");
        cookie.setMaxAge(0);
        cookie.setPath(ssoOpthion.domain);
        cookie.setPath(ssoOpthion.rootPath);
        response.addCookie(cookie);
    }


}
