package com.zg.handler;

import com.zg.database.util.JDBCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/24 0024.
 */
public class CommitInterfaceHandler implements InvocationHandler {
    private static final Logger LOGGER= LoggerFactory.getLogger(CommitInterfaceHandler.class);
    private Object target;
    private List methodList = new ArrayList();

    public CommitInterfaceHandler(Object target, String method) {
        this.target = target;
        for (String m : method.split(",")) {
            if("ALL".equals(m)) {

            }else{
                methodList.add(m);
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object result = null;
        try {
            result = method.invoke(target, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (methodList.contains(method.getName())) {
            LOGGER.info(method.getName() + " 事务被提交");
            JDBCUtils.commit();
            return result;
        } else {
           // LOGGER.info(method.getName() + " 事务未被提交");
            JDBCUtils.release();
            return result;
        }

    }
}
