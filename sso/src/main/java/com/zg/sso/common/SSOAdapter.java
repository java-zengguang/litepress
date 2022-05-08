package com.zg.sso.common;

import com.zg.sso.entity.SSOOpthion;
import com.zg.common.util.url.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SSOAdapter {
    private static SSOAdapter ssoAdapter;
    private SSOOpthion ssoOpthion;
    private LoginInte loginService;
    private static final Logger logger = LoggerFactory.getLogger(WebCacheLogin.class);

    private SSOAdapter(SSOOpthion ssoOpthion, LoginInte loginInte) {
        this.loginService = loginInte;
        this.ssoOpthion = ssoOpthion;
    }

    public static SSOAdapter getInstanse(SSOOpthion ssoOpthion, LoginInte loginInte) {
        ssoAdapter = new SSOAdapter(ssoOpthion, loginInte);
        return ssoAdapter;
    }


    private boolean isLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        Map<String, String> cookieMap = getCookies(request);
        String token = cookieMap.get("token");
        String uuid = cookieMap.get("uuid");
        return loginService.isLogin(token, uuid);

    }


    public boolean doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

        String uri = ((HttpServletRequest) request).getRequestURI();

        boolean loginStatus = isLogin((HttpServletRequest) request, (HttpServletResponse) response);
        if (loginStatus) {
            logger.info("通过验证");
            return true;
        } else {
            logger.info("未通过验证");
            Map<String, String> cookieMap = getCookies((HttpServletRequest) request);
            String token = cookieMap.get("token");
            toLogin((HttpServletResponse) response, token);
            return false;
        }
    }


    private Map<String, String> getCookies(HttpServletRequest request) throws IOException {
        Map<String, String> resultMap = new HashMap<>();
        Cookie readCookies[] = (request).getCookies();

        if (readCookies != null) {
            for (Cookie cookie : readCookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                if ("token".equals(cookieName)) {
                    resultMap.put(cookieName, cookieValue);
                }
                if ("uuid".equals(cookieName)) {
                    resultMap.put(cookieName, cookieValue);
                }
            }
        }
        if (resultMap.get("token") == null || resultMap.get("uuid") == null) {
            resultMap.put("token", toRegistToken(request));
        }
        return resultMap;

    }


    private String toRegistToken(HttpServletRequest request) throws IOException {

        String url = (request).getRequestURL().toString();
        url = URLUtils.getURLEncoderString(url);
        logger.info(url);

        String newToken = loginService.registToken(url, ssoOpthion.domain, ssoOpthion.rootPath);
        return newToken;
    }

    private void toLogin(HttpServletResponse response, String token) throws IOException {

        String domain = ssoOpthion.domain;
        String rootPath = ssoOpthion.rootPath;
        Cookie cookie = new Cookie("token", token);
        cookie.setDomain(domain);
        cookie.setPath(rootPath);
        cookie.setMaxAge(30 * 60);
        response.addCookie(cookie);
        response.sendRedirect(ssoOpthion.ssoURL + ssoOpthion.loginPath);
        return;
    }

}
