package com.zg.sso.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginInte {

    boolean isLogin(String token, String uuid);

    Object doLogin(HttpServletRequest request, String uuid, HttpServletResponse response);

    String registToken(String url, String domain, String rootPath);
}
