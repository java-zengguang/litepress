package com.zg.sso.common;

public interface LoginInte {

    boolean isLogin(String token,String uuid);
    boolean doLogin(String token,String uuid);
    String registToken(String url, String domain, String rootPath);
}
