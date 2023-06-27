package com.zg.login.provider;

import com.zg.common.proxy.ProxyUtils;
import com.zg.direction.annotation.Provider;
import com.zg.login.inte.LoginServiceInte;
import com.zg.login.service.LoginService;

import java.util.Map;

@Provider(providerName = "/login")
public class LoginProvider implements LoginServiceInte {
    LoginServiceInte loginService = (LoginServiceInte) ProxyUtils.getServiceProxy(new LoginService());

    @Override
    public String verification(String username, String passworld) {
        return loginService.verification(username, passworld);
    }

    @Override
    public Map<String, String> login(String password, String username, String token) {
        return loginService.login(password, username, token);
    }

    @Override
    public String registToken(String url, String domain, String rootPath) {
        return loginService.registToken(url, domain, rootPath);
    }

    @Override
    public Integer isLogin(String token, String uuid) {
        return loginService.isLogin(token, uuid);
    }

    @Override
    public Map<String, String> getTokenValue(String token) {
        return loginService.getTokenValue(token);
    }

    @Override
    public Map<String, String> getTokenValue(String token, String del_flag) {
        return loginService.getTokenValue(token, del_flag);
    }

    @Override
    public void invalidToken(String token) {
        loginService.invalidToken(token);
    }

    @Override
    public Integer updateLoginValid(String uuid, String token) {
        return loginService.updateLoginValid(uuid, token);
    }

    @Override
    public Integer updateLoginInvalid(String uuid, String token) {
        return loginService.updateLoginInvalid(uuid, token);
    }
}
