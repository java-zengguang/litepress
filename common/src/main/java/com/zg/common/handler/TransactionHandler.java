package com.zg.common.handler;

import com.zg.common.annotation.Transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/12/24 0024.
 */
public class TransactionHandler implements InvocationHandler {
    private final Object target;


    public TransactionHandler(Object target) {
        this.target = target;

    }

    public void commit() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = target.getClass().getSuperclass();
        Method method = clazz.getMethod("commit");
        method.invoke(target);
    }

    public void release() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz = target.getClass().getSuperclass();
        Method method = clazz.getMethod("release");
        method.invoke(target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        try {

            result = method.invoke(target, args); //调用业务类（父类中）的方法
            if (method.isAnnotationPresent(Transaction.class)) {
                commit();
            }
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } finally {
            release();
        }
        return result;

    }
}
