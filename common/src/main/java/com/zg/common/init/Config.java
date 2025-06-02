package com.zg.common.init;

import com.zg.common.bean.factory.BeanFactory;
import com.zg.common.util.reflect.TransEntityTypeUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/12/14 0014.
 */
public class Config {
    public static final int ERROR_REPEAT = 3;
    public static final Map configMap = new ConcurrentHashMap();
    public static CountDownLatch count = new CountDownLatch(ERROR_REPEAT);




    private static synchronized void createConfigMap(String[] array) {
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
                String[] array = {beanName};
                createConfigMap(array);
                object = getConfig(beanName);
            } else {
                count = new CountDownLatch(ERROR_REPEAT);
            }
        }
        return object;
    }


    public static synchronized void setConfig(String beanName, Object obj) {
        configMap.put(beanName, obj);
    }

    public static synchronized boolean containsKey(String beanName) {
        return configMap.containsKey(beanName);
    }

    public static void updateBean2ConfigProperties(String beanConfigProperties) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Properties properties = new Properties();
        properties.load(new StringReader(beanConfigProperties));
        String beanName = (String) properties.get("beanName");
        String beanType = (String) properties.get("beanType");
        Object obj = configMap.get(beanName);
        if (obj == null) {
            obj = Class.forName(beanType).getDeclaredConstructor().newInstance();
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            String name = field.getName();
            String value = properties.getProperty(name);
            if (value != null) {
                Object objValue = TransEntityTypeUtils.translateType(value, field.getType());
                field.set(obj, objValue);
            }


        }
        configMap.put(beanName, obj);

    }

}
