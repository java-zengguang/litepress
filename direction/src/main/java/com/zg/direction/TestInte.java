package com.zg.direction;

import java.util.ArrayList;
import java.util.List;

public interface TestInte {

    TestEntity hello() throws InterruptedException;

    TestEntity hello1(String name);

    TestEntity hello2(TestEntity name);

    TestEntity hello3(TestEntity name, String x);

    List<TestEntity> hello4(ArrayList<TestEntity> name, String x);


}
