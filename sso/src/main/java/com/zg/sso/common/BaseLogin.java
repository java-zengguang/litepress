package com.zg.sso.common;

public class BaseLogin implements LoginInte {

    @Override
    public boolean isLogin(String token, String uuid) {
        return false;
    }

    @Override
    public boolean doLogin(String token, String uuid) {
        return false;
    }

    @Override
    public String registToken(String url, String domain, String rootPath) {
        return null;
    }


}
