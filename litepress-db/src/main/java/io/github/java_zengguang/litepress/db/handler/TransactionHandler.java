package io.github.java_zengguang.litepress.db.handler;

import io.github.java_zengguang.litepress.core.annotation.Transaction;
import io.github.java_zengguang.litepress.db.dao.manager.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;


public class TransactionHandler implements InvocationHandler {
    private final Object target;
    private TransactionManager transactionManager;


    private static final ThreadLocal<AtomicInteger> threadLocalTxDP = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> threadLocalConnDP = new ThreadLocal<>();


    public TransactionHandler(Object target) {
        this.target = target;
        this.transactionManager = TransactionManager.getInstance();
    }

    public void commit() throws SQLException {
        transactionManager.commit();
    }

    public void release() throws SQLException {
        transactionManager.release();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        try {
            if (method.isAnnotationPresent(Transaction.class) && threadLocalTxDP.get() == null) {
                threadLocalTxDP.set(new AtomicInteger(0));
            }
            if (threadLocalConnDP.get() == null) {
                threadLocalConnDP.set(new AtomicInteger(0));
            }
            if (threadLocalTxDP.get() != null) {
                threadLocalTxDP.get().incrementAndGet();
            }
            threadLocalConnDP.get().incrementAndGet();

            result = method.invoke(target, args);

            if (threadLocalTxDP.get() != null) {
                if (threadLocalTxDP.get().decrementAndGet() < 1) {
                    commit();
                    threadLocalTxDP.remove();
                }
            }
        } catch (InvocationTargetException e) {
            if (threadLocalTxDP.get() != null) {
                if (threadLocalTxDP.get().decrementAndGet() < 1) {
                    threadLocalTxDP.remove();
                }
            }
            throw e.getCause();
        } finally {
            if (threadLocalConnDP.get().decrementAndGet() < 1) {
                release();
                threadLocalConnDP.remove();
            }
        }
        return result;

    }
}
