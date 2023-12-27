package com.zg.network.im.login;

import com.zg.login.inte.LoginServiceInte;

public class LoginFactory {

    private static LoginServiceInte loginServiceInte = null;

    private LoginFactory() {
    }

    public static LoginServiceInte getLoginService() {
        if (loginServiceInte == null) {
            Class[] classes = new Class[1];
            loginServiceInte = new SimpleLoginService();
        }
        return loginServiceInte;
    }
}
