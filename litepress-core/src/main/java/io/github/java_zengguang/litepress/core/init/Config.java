package io.github.java_zengguang.litepress.core.init;

import io.github.java_zengguang.litepress.core.bean.factory.BeanFactory;
import io.github.java_zengguang.litepress.core.util.reflect.TransEntityTypeUtils;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public class Config {
    private static Map configMap = new ConcurrentHashMap();


    public static synchronized void initConfig(String configString) {
        BeanFactory beanFactory = new BeanFactory(configString);
        Map objectMap = beanFactory.createAllBeans();
        configMap.putAll(objectMap);

    }


    public static synchronized Object getConfig(String beanName) {
        Object object = configMap.get(beanName);
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
