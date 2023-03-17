package com.zg.direction;

import com.zg.common.util.CommonUtil;
import com.zg.direction.adapter.ProviderAdapter;
import com.zg.direction.proxy.ConsumerHandler;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestConsumer {

    public static void main(String args[]) {
        String rootPath= CommonUtil.getThisPath(TestConsumer.class);
        System.setProperty("projectRootPath",rootPath);

        Class[] classes = new Class[1];
        classes[0] = TestInte.class;
        TestInte test = (TestInte) Proxy.newProxyInstance(TestInte.class.getClassLoader(), classes, new ConsumerHandler("/Test/good"));
           TestEntity testEntity = test.hello1("你好");
        System.out.println("===="+testEntity);
        System.out.println(test.hello2(testEntity));
        test.hello3(testEntity,"xxx");
       System.out.println(test.hello4(new ArrayList(Arrays.asList(testEntity)),"xxx"));

       Runnable runnable=new Runnable() {
           @Override
           public void run() {
               TestInte test = (TestInte) Proxy.newProxyInstance(TestInte.class.getClassLoader(), classes, new ConsumerHandler("/Test"));
               try {
                   System.out.println("================="+test.hello());
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
       };
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executorService.submit(runnable);
        }


    }
}
