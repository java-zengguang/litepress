package com.zg.sso.common;

import com.zg.cache.util.RomCacheInte;
import com.zg.cache.util.RomCacheUtil;
import com.zg.mvc.entity.MessageBean;
import com.zg.sso.entity.UserLogin;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WebCacheLogin extends BaseLogin {
    private static WebCacheLogin webCacheLogin = new WebCacheLogin();
    private RomCacheInte romCacheInte = RomCacheUtil.getRomCache("LoginCache");

    private WebCacheLogin() {
    }

    public static WebCacheLogin getInstance() {
        return webCacheLogin;
    }

    @Override
    public boolean isLogin(String token, String uuid) {
        List list = null;
        if (token == null || uuid == null) {
            return false;
        } else {
            list = romCacheInte.findModel("token=" + token, "uuid=" + uuid);
        }
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;

        }

    }

    @Override
    public Object doLogin(HttpServletRequest
                                  request, String uuid, HttpServletResponse response) {
        String token = getCookieValue("token", request);
        List<UserLogin> list = romCacheInte.findModel("token=" + token);
        if (token != null && list != null && list.size() > 0) {
            UserLogin userLogin = list.get(0);
            userLogin.uuid = uuid;
            romCacheInte.addModel(userLogin);
            List<Cookie> newCookieList = new ArrayList();
            Cookie userCookie = new Cookie("uuid", uuid);
            newCookieList.add(userCookie);
            Cookie tokenCookie = new Cookie("token", token);
            newCookieList.add(tokenCookie);
            setCookies(response, newCookieList, userLogin.domain, userLogin.rootPath, userLogin.url);
            String url = new String(userLogin.url);
            System.out.println("url=" + url);
            return new MessageBean("操作成功", true, url);

        } else {
            clearCookie(response, "token");
            clearCookie(response, "uuid");
            return new MessageBean("token丢失", false, null);
        }
    }

    @Override
    public String registToken(String url, String domain, String rootPath) {
        UUID random = UUID.randomUUID();
        String token = "" + random.toString();
        UserLogin userLogin = new UserLogin();
        userLogin.token = token;
        userLogin.url = url;
        userLogin.domain = domain;
        userLogin.rootPath = rootPath;
        romCacheInte.addModel(userLogin);
        return token;
    }


    public UserLogin logout(String token, String uuid) {

        List<UserLogin> list = romCacheInte.findModel("token=" + token, "uuid=" + uuid);
        if (list != null && list.size() > 0) {
            romCacheInte.deleteModel("token=" + token, "uuid=" + uuid);
            return list.get(0);
        } else {
            return null;
        }
    }
}
