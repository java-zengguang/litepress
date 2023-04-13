package com.zg.login;

import com.zg.direction.proxy.ConsumerHandler;
import com.zg.login.inte.LoginServiceInte;

import java.lang.reflect.Proxy;

public class TestCustmer {

    public static void main(String args[]) {
        Class[] classes = new Class[1];
        classes[0] = LoginServiceInte.class;
        LoginServiceInte loginServiceInte = (LoginServiceInte) Proxy.newProxyInstance(LoginServiceInte.class.getClassLoader(), classes, new ConsumerHandler("/login"));
        System.out.println("------" + loginServiceInte.registToken("121212", "12121", "12345nimeide"));
        System.exit(-1);
    }
}
