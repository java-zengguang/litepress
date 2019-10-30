package com.zg.direction;

import com.zg.direction.proxy.ConsumerHandler;

import java.lang.reflect.Proxy;
import java.util.List;

public class TestConsumer {

    public static void main(String args[]){
        Class [] classes=new Class[1];
        classes[0]=TestInte.class;
        TestInte test=(TestInte) Proxy.newProxyInstance(TestInte.class.getClassLoader(),classes,new ConsumerHandler("/Test"));
        TestEntity testEntity=test.hello();
        System.out.println("result="+testEntity);
        System.exit(-1);
    }
}
