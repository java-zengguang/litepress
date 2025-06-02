package com.zg.litepress.db.handler;

import org.tinylog.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ServiceHandler implements InvocationHandler {

    private final Object target;
    private final List methodList = new ArrayList();

    public ServiceHandler(Object target, String method) {
        this.target = target;
        for (String m : method.split(",")) {
            methodList.add(m);
        }

    }

    @Override
    public Object invoke(Object proxy, Method currentMethod, Object[] args)
            throws Throwable {

        Object result = currentMethod.invoke(target, args); //执行被代理方法
        if (methodList.contains(currentMethod.getName())) {
            //执行代理
            Method proxyMothod = target.getClass().getMethod("submit"); //获取代理方法
            Logger.info(ServiceHandler.class + "====" + currentMethod.getName() + "被" + proxyMothod.getName() + "代理");
            try {
                proxyMothod.invoke(target);  //执行代理
            } catch (InvocationTargetException e) {
                e.getCause();
            }
        } else {

            Logger.info(ServiceHandler.class + "====" + currentMethod.getName() + "没有被代理");

        }
        return result;
    }

}
