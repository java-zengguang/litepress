package io.github.java_zengguang.litepress.direction.util;

import io.github.java_zengguang.litepress.direction.proxy.ConsumerHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


public class ProxyUtils {


    public static Object getRemotingProxy(Class<?> interfaceClass, String providerName) {
        Object targetObject = null;
        try {
            InvocationHandler proxyObject = ConsumerHandler.class.getDeclaredConstructor(String.class).newInstance(providerName);
            targetObject = Proxy.newProxyInstance(ConsumerHandler.class.getClassLoader(), new Class[]{interfaceClass}, proxyObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return targetObject;
    }


}
