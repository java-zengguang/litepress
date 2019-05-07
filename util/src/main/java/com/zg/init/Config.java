package com.zg.init;

import com.zg.bean.factory.BeanFactory;

import java.util.*;

/**
 * Created by Administrator on 2018/12/14 0014.
 */
public class Config {
    public static Map configMap=new HashMap<>();
    public static final int ERROR_REPEAT=3;
    public static int count=ERROR_REPEAT;

    private static void createConfigMap(String array[]){
        for(String beanName:array){
            Object object=BeanFactory.createBean(beanName);
            if(object!=null){
                configMap.put(beanName,object);
            }
        }
    }

    public static Object getConfig(String beanName){
        Object object=configMap.get(beanName);
        if(count>0) {
            if (object == null) {
                count--;
                System.out.println("初始化"+ beanName );
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
