package com.zg.sso.filter;


import com.zg.common.init.Config;
import com.zg.sso.entity.SSOOpthion;
import com.zg.sso.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/13 0013.
 */
public class WebLoginFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(WebLoginFilter.class);

    LoginService loginService = new LoginService();

    private SSOOpthion ssoOpthion = (SSOOpthion) Config.getConfig("SSOOpthion");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    public int isLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        Map<String, String> cookieMap = getCookies(request);
        String token = cookieMap.get("token");
        String uuid = cookieMap.get("uuid");
        return loginService.isLogin(token, uuid);

    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String uri = ((HttpServletRequest) request).getRequestURI();
    /*  if(uri.contains("/sso/login.do") || uri.contains("/sso/toLogin.do") ){

            chain.doFilter(request, response);
    }else{*/

        int loginStatus = isLogin((HttpServletRequest) request, (HttpServletResponse) response);
        if (loginStatus > 0) {
            logger.info("通过验证");
            chain.doFilter(request, response);
        } else {
            logger.info("未通过验证");
            Map<String, String> cookieMap = getCookies((HttpServletRequest) request);
            String token = cookieMap.get("token");
            toLogin((HttpServletResponse) response, token);
        }
    }

    /*       }*/
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
        response.sendRedirect(ssoOpthion.ssoURL + "/sso/toLogin.do");
        return;
    }

    @Override
    public void destroy() {

    }
}
