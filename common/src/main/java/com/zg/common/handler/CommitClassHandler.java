package com.zg.common.handler;


import com.zg.common.dao.database.NewDBPUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/12/24 0024.
 */
public class CommitClassHandler extends BaseClassHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommitClassHandler.class);

    private Object target;
    private List methodList = new ArrayList();
    private boolean commitAll = false;

    public CommitClassHandler() {
    }

    public Object getInstance(Object target, String method) {
        this.target = target;

        for (String m : method.split(",")) {
            if ("ALL".equals(m)) {
                commitAll = true;
            } else {
                methodList.add(m);
            }
        }
        Enhancer enhancer = new Enhancer(); //创建加强器，用来创建动态代理类
        enhancer.setSuperclass(this.target.getClass());  //为加强器指定要代理的业务类（即：为下面生成的代理类指定父类）
        //设置回调：对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实现intercept()方法进行拦
        enhancer.setCallback(this);
        // 创建动态代理类对象并返回
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Exception {
        Object result = null;

        try {
            result = methodProxy.invokeSuper(o, objects); //调用业务类（父类中）的方法
            if (commitAll || methodList.contains(method.getName())) {
                logger.info(method.getName() + " 事务被提交");
                NewDBPUtils.commit("optionDB");
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new Exception("事务提交失败！");
        } finally {
            NewDBPUtils.release("optionDB");
        }

        return result;
    }


}
