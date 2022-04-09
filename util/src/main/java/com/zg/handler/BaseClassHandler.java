package com.zg.handler;

import net.sf.cglib.proxy.MethodInterceptor;

public abstract class BaseClassHandler implements MethodInterceptor {
    public abstract Object getInstance(Object object, String methods);
}
