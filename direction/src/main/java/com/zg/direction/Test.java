package com.zg.direction;

import com.zg.direction.annotation.Provider;

import java.util.ArrayList;
import java.util.List;

@Provider(providerName = "/Test")
public class Test implements TestInte {

    public TestEntity hello() {
        System.out.println("hello");
        List list = new ArrayList();
        list.add("1212");
        list.add("1213");

        TestEntity testEntity = new TestEntity();
        testEntity.list = list;
        return testEntity;
    }

    public TestEntity hello(String name) {
        System.out.println("hello");
        TestEntity testEntity = new TestEntity();
        testEntity.setS("hello  " + name);
        return testEntity;
    }

}
