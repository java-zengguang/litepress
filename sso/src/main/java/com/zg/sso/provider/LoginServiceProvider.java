package com.zg.sso.provider;

import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.sso.service.LoginService;
import com.zg.sso.service.LoginServiceInte;

public class LoginServiceProvider extends LoginService {

    private static LoginServiceInte loginService= (LoginServiceInte) ProxyUtils.getProxyInterface(LoginService.class,new CommitInterfaceHandler(new LoginService(),"login,loginout,registToken,updateLoginInvalid"));

    private LoginServiceProvider(){}

    public static LoginServiceInte getInstance(){
        return loginService;
    }
}
