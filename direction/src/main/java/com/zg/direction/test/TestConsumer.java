package com.zg.direction.test;

import com.zg.common.init.Evn;
import com.zg.direction.proxy.ConsumerHandler;
import org.tinylog.Logger;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestConsumer {

    public static void main(String[] args) {
        String rootPath = Evn.getModulePath();
        System.setProperty("projectRootPath", rootPath);

        Class[] classes = new Class[1];
        classes[0] = TestInte.class;
        TestInte test = (TestInte) Proxy.newProxyInstance(TestInte.class.getClassLoader(), classes, new ConsumerHandler("/Test/good"));
        TestEntity testEntity = test.hello1("你好");
        Logger.info("====" + testEntity);
        Logger.info(test.hello2(testEntity));
        test.hello3(testEntity, "xxx");
        Logger.info(test.hello4(new ArrayList(Arrays.asList(testEntity)), "xxx"));

        Runnable runnable = () -> {
            TestInte test1 = (TestInte) Proxy.newProxyInstance(TestInte.class.getClassLoader(), classes, new ConsumerHandler("/Test"));
            try {
                Logger.info("=================" + test1.hello());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executorService.submit(runnable);
        }


    }
}
