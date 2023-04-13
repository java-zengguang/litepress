package com.zg.common.init;

import com.zg.common.bean.factory.BeanFactory;
import org.tinylog.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/12/14 0014.
 */
public class Config {
    public static final int ERROR_REPEAT = 3;
    public static Map configMap = new ConcurrentHashMap();

    public static CountDownLatch count = new CountDownLatch(ERROR_REPEAT);

    private static synchronized void createConfigMap(String array[]) {
        for (String beanName : array) {
            Object object = BeanFactory.createBean(beanName);
            if (object != null) {
                configMap.put(beanName, object);
            }
        }
    }

    public static synchronized Object getConfig(String beanName) {
        Object object = configMap.get(beanName);
        if (count.getCount() > 0) {
            if (object == null) {
                count.countDown();
                Logger.info("初始化" + beanName);
                String array[] = {beanName};
                createConfigMap(array);
                object = getConfig(beanName);
            } else {
                count = new CountDownLatch(ERROR_REPEAT);
            }
        }
        return object;
    }


}
