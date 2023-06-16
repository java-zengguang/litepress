package com.zg.common.handler;

import com.zg.common.annotation.Transaction;
import com.zg.common.dao.database.NewDBPUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2018/12/24 0024.
 */
public class TransactionHandler implements InvocationHandler {
    private Object target;

    public TransactionHandler(Object target) {
        this.target = target;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        Object result = null;

        try {
            result = method.invoke(target, args); //调用业务类（父类中）的方法
            if (method.isAnnotationPresent(Transaction.class)) {
                NewDBPUtils.commit();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            new Exception("事务提交失败");
        } finally {
            NewDBPUtils.release();
        }
        return result;

    }
}
