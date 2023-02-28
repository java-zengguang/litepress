package com.zg.direction;

import com.zg.direction.annotation.Provider;

import java.util.ArrayList;
import java.util.List;

@Provider(providerName = "/Test")
public class TestServer implements TestInte{
    @Override
    public TestEntity hello() throws InterruptedException {
        System.out.println("你好");
        TestEntity testEntity=new TestEntity();
        testEntity.s = "hello";

        return testEntity;
    }

    @Override
    public TestEntity hello1(String name) {
        TestEntity testEntity=new TestEntity();
        testEntity.s=name;
        return testEntity;
    }

    @Override
    public TestEntity hello2(TestEntity name) {

        System.out.println(name);
        return name;
    }

    @Override
    public TestEntity hello3(TestEntity name,String x) {

        System.out.println(name);
        return name;
    }

    @Override
    public List<TestEntity> hello4(ArrayList<TestEntity> name, String x) {
        System.out.println(name);
        return name;
    }
}
