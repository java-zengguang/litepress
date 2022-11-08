package com.zg.direction;

import com.zg.direction.register.ZookeeperUtil;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TestInte {

    TestEntity hello();

    TestEntity hello1(String name);
    TestEntity hello2(TestEntity name);
    TestEntity hello3(TestEntity name,String x);
    List<TestEntity> hello4(ArrayList<TestEntity> name, String x);


}
