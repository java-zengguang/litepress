package com.zg.common.handler;

import com.zg.common.dao.database.NewDBPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/24 0024.
 */
public class CommitInterfaceHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommitInterfaceHandler.class);
    private Object target;
    private List methodList = new ArrayList();
    private boolean commitAll=false;
    private String dataSource;

    public CommitInterfaceHandler(String dataSource,Object target, String method) {
        this.target = target;
        this.dataSource=dataSource;
        for (String m : method.split(",")) {
            if ("ALL".equals(m)) {
                commitAll=true;
            } else {
                methodList.add(m);
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        Object result = null;

        try {
            result = method.invoke(target, args); //调用业务类（父类中）的方法
            if (commitAll || methodList.contains(method.getName())) {
                logger.info(method.getName() + " 事务被提交");
                NewDBPUtils.commit(dataSource);
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new Exception("事务提交失败！");
        } finally {
            NewDBPUtils.release(dataSource);
        }
        return result;

    }
}
