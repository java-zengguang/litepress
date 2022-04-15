package com.zg.init;

import com.zg.bean.factory.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/14 0014.
 */
public class Config {
    public static final int ERROR_REPEAT = 3;
    private static final Logger logger = LoggerFactory.getLogger(Config.class.getName());
    public static Map configMap = new HashMap<>();
    public static int count = ERROR_REPEAT;

    private static void createConfigMap(String array[]) {
        for (String beanName : array) {
            Object object = BeanFactory.createBean(beanName);
            if (object != null) {
                configMap.put(beanName, object);
            }
        }
    }

    public static Object getConfig(String beanName) {
        Object object = configMap.get(beanName);
        if (count > 0) {
            if (object == null) {
                count--;
                logger.info("初始化" + beanName);
                String array[] = {beanName};
                createConfigMap(array);
                object = getConfig(beanName);
            } else {
                count = ERROR_REPEAT;
            }
        }
        return object;
    }

}
