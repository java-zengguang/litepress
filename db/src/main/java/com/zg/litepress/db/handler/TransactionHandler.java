package com.zg.litepress.db.handler;

import com.zg.litepress.core.annotation.Transaction;
import com.zg.litepress.db.dao.database.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * Created by Administrator on 2018/12/24 0024.
 */
public class TransactionHandler implements InvocationHandler {
    private final Object target;


    public TransactionHandler(Object target) {
        this.target = target;

    }

    public void commit() throws  InvocationTargetException,  SQLException {

        TransactionManager.commit();
    }

    public void release() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException, ClassNotFoundException {
        TransactionManager.release();
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
