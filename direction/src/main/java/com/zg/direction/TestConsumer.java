package com.zg.direction;

import com.zg.common.util.CommonUtil;
import com.zg.direction.adapter.ProviderAdapter;
import com.zg.direction.proxy.ConsumerHandler;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

public class TestConsumer {

    public static void main(String args[]) {
        String rootPath= CommonUtil.getThisPath(TestConsumer.class);
        System.setProperty("projectRootPath",rootPath);

        Class[] classes = new Class[1];
        classes[0] = TestInte.class;
        TestInte test = (TestInte) Proxy.newProxyInstance(TestInte.class.getClassLoader(), classes, new ConsumerHandler("/Test"));
           TestEntity testEntity = test.hello1("你好");
        System.out.println("===="+testEntity);
        System.out.println(test.hello2(testEntity));
        test.hello3(testEntity,"xxx");
       System.out.println(test.hello4(new ArrayList(Arrays.asList(testEntity)),"xxx"));
        System.exit(-1);
    }
}
