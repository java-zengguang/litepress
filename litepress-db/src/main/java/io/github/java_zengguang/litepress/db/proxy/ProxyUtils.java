package io.github.java_zengguang.litepress.db.proxy;

import io.github.java_zengguang.litepress.core.error.SysException;
import io.github.java_zengguang.litepress.db.handler.TransactionHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2019/3/14 0014.
 */
public class ProxyUtils {

    public static Object getServiceProxy(Object obj) {
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new TransactionHandler(obj));
    }


}
