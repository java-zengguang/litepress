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

    public static Object getRemotingProxy(Class interfaceClass, String providerName) {
        Object targetObject = null;
        try {
            Class<InvocationHandler> remotingClass = (Class<InvocationHandler>) Class.forName("com.zg.direction.proxy.ConsumerHandler");
            InvocationHandler proxyObject = remotingClass.getDeclaredConstructor(String.class).newInstance(providerName);
            Class[] interfaceArrays = {interfaceClass};
            targetObject = Proxy.newProxyInstance(remotingClass.getClassLoader(), interfaceArrays, proxyObject);
        } catch (ClassNotFoundException e) {
            throw new SysException("未找到驱动 com.zg.direction.proxy.ConsumerHandler 请确认是否引入com.zg.direction 模块" + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return targetObject;
    }


}
