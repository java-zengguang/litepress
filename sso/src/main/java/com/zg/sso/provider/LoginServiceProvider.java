package com.zg.sso.provider;

import com.zg.common.handler.CommitInterfaceHandler;
import com.zg.common.proxy.ProxyUtils;
import com.zg.sso.service.LoginService;
import com.zg.sso.service.LoginServiceInte;

public class LoginServiceProvider extends LoginService {

    private static LoginServiceInte loginService = (LoginServiceInte) ProxyUtils.getServiceProxy(new LoginService());

    private LoginServiceProvider() {
    }

    public static LoginServiceInte getInstance() {
        return loginService;
    }
}
