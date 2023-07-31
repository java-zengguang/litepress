package com.zg.direction;

import com.zg.direction.annotation.Provider;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

@Provider(providerName = "/Test1/good")
public class TestServer1 implements TestInte {
    @Override
    public TestEntity hello() {
        Logger.info("你好");
        TestEntity testEntity = new TestEntity();
        testEntity.s = "hello";
        return testEntity;
    }

    @Override
    public TestEntity hello1(String name) {
        TestEntity testEntity = new TestEntity();
        testEntity.s = name;
        return testEntity;
    }

    @Override
    public TestEntity hello2(TestEntity name) {

        Logger.info(name);
        return name;
    }

    @Override
    public TestEntity hello3(TestEntity name, String x) {

        Logger.info(name);
        return name;
    }

    @Override
    public List<TestEntity> hello4(ArrayList<TestEntity> name, String x) {
        Logger.info(name);
        return name;
    }
}
