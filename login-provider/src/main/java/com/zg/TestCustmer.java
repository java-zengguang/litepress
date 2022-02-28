package com.zg;

import com.zg.direction.TestEntity;
import com.zg.direction.TestInte;
import com.zg.direction.proxy.ConsumerHandler;
import com.zg.inte.LoginServiceInte;

import java.lang.reflect.Proxy;

public class TestCustmer {

    public static void main(String args[]){
        Class [] classes=new Class[1];
        classes[0]= LoginServiceInte.class;
        LoginServiceInte loginServiceInte=(LoginServiceInte) Proxy.newProxyInstance(LoginServiceInte.class.getClassLoader(),classes,new ConsumerHandler("/login"));
        loginServiceInte.registToken("121212","12121","12345nimeide");
        System.exit(-1);
    }
}
