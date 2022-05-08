package com.zg.common.proxy;

import com.zg.common.handler.BaseClassHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2019/3/14 0014.
 */
public class ProxyUtils {
    public static Object getProxyInterface(Class targetClass, InvocationHandler handler) {
        Object object = null;
        object = Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), handler);
        return object;
    }

    public static Object getProxyClass(BaseClassHandler object, String methods) {

        return object.getInstance(object, methods);
    }

}
