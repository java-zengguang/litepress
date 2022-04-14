package com.zg.sso.utils;

import com.zg.cache.util.RomCacheInte;
import com.zg.cache.util.RomCacheUtil;
import com.zg.mvc.entity.MessageBean;
import com.zg.sso.common.WebCacheLogin;
import com.zg.sso.entity.UserLogin;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SSOUtils {

    public static WebCacheLogin webCacheLogin = WebCacheLogin.getInstance();
    private static RomCacheInte romCache = RomCacheUtil.getRomCache("LoginCache");
    private static final Logger LOGGER = Logger.getLogger(SSOUtils.class.getName());


    public static MessageBean signOut(HttpServletRequest request, HttpServletResponse response) {

        String token = getCookieValue("token", request);
        String uuid = getCookieValue("uuid", request);
        UserLogin userLogin = webCacheLogin.logout(uuid, token);
        String url = "";
        if (userLogin != null) {
            clearCookie(response, "token", userLogin.domain, userLogin.rootPath);
            clearCookie(response, "uuid", userLogin.domain, userLogin.rootPath);
            url = userLogin.url;
        }
        return new MessageBean("操作成功", true, url);
    }

    public static MessageBean login(String uuid, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, NoSuchFieldException, SQLException, IOException {
        MessageBean messageBean = null;
        String url = null;
        String domain = null;
        String rootPath = null;
        List<Cookie> newCookieList = new ArrayList();
        String token = getCookieValue("token", request);
        List<UserLogin> list = romCache.findModel("token=" + token);
        if (token == null || list == null || list.size() <= 0) {

        } else {
            webCacheLogin.doLogin(request, uuid, response);
            UserLogin userLogin = list.get(0);
            url = userLogin.url;
            domain = userLogin.domain;
            rootPath = userLogin.rootPath;
            if (uuid != null) {
                Cookie userCookie = new Cookie("uuid", uuid);
                newCookieList.add(userCookie);
                Cookie tokenCookie = new Cookie("token", token);
                newCookieList.add(tokenCookie);
                messageBean = new MessageBean("操作成功", true, url);
            } else {
                messageBean = new MessageBean("操作失败", false, url);
            }
            setCookies(response, newCookieList, domain, rootPath, url);

        }
        return messageBean;
    }

    private static String getCookieValue(String key, HttpServletRequest request) {

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

    private static Cookie getCookie(String key, HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private static void setCookies(HttpServletResponse response, List<Cookie> cookieList, String domain, String rootPath, String url) throws IOException {
        for (Cookie cookie : cookieList) {
            cookie.setDomain(domain);
            cookie.setPath(rootPath);
            cookie.setMaxAge(30 * 60);
            response.addCookie(cookie);
        }
    }

    private static void clearCookie(HttpServletResponse response, String key, String domain, String rootPath) {
        Cookie cookie = new Cookie(key, "");
        cookie.setMaxAge(0);
        cookie.setPath(domain);
        cookie.setPath(rootPath);
        response.addCookie(cookie);
    }
}
