package com.zg.direction;

import com.zg.direction.proxy.ConsumerHandler;

import java.lang.reflect.Proxy;

public class TestConsumer {

    public static void main(String args[]) {
        Class[] classes = new Class[1];
        classes[0] = TestInte.class;
        TestInte test = (TestInte) Proxy.newProxyInstance(TestInte.class.getClassLoader(), classes, new ConsumerHandler("/Test"));
        TestEntity testEntity = test.hello("你好");
        System.exit(-1);
    }
}
