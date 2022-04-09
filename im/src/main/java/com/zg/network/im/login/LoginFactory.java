package com.zg.network.im.login;

import com.zg.inte.LoginServiceInte;

public class LoginFactory {

    private static LoginServiceInte loginServiceInte = null;

    private LoginFactory() {
    }

    public static LoginServiceInte getLoginService() {
        if (loginServiceInte == null) {
            Class[] classes = new Class[1];
          /*  classes[0] = LoginServiceInte.class;
            loginServiceInte = (LoginServiceInte) Proxy.newProxyInstance(TestInte.class.getClassLoader(), classes, new ConsumerHandler("/login"));
*/
            loginServiceInte = new SimpleLoginService();
        }
        return loginServiceInte;
    }
}
