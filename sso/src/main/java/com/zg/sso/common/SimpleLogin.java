package com.zg.sso.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleLogin extends BaseLogin {
    @Override
    public boolean isLogin(String token, String uuid) {
        return false;
    }

    @Override
    public Object doLogin(HttpServletRequest request, String uuid, HttpServletResponse response) {
        return null;
    }

    @Override
    public String registToken(String url, String domain, String rootPath) {
        return null;
    }
}
