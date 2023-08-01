package com.zg.chain.common.factory;

import com.zg.common.annotation.ScanAnnotation;
import com.zg.chain.common.components.Component;
import com.zg.chain.common.components.Components;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ComponentsFactory {
    private final static ComponentsFactory driverFactory = new ComponentsFactory();
    private final static Map<String, Class> componentClassMap = new HashMap<>();

    private ComponentsFactory() {

    }

    public static ComponentsFactory newInstance() {
        return driverFactory;
    }

    public Component getComponent(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        //  String packages = System.getProperty("scanPackage");

        Class componentClass = componentClassMap.get(name);
        if (componentClass == null) {
            Set<Class<?>> classList = ScanAnnotation.getClassFromAnn(Components.class);
            for (Class classes : classList) {
                Components components = (Components) classes.getAnnotation(Components.class);
                if (components != null && components.equals(components.name())) {
                    componentClass = classes;
                    componentClassMap.put(components.name(), componentClass);
                }
            }
        }

        Component component = (Component) componentClass.newInstance();

        return component;
    }


    public Component getComponent(String name, String systemFlag, String functionFlag, Map<String, Object> initParamMap, Map<String, Object> beanMap) throws Exception {
        //  String packages = System.getProperty("scanPackage");

        Class componentClass = componentClassMap.get(name);
        if (componentClass == null) {
            Set<Class<?>> classList = ScanAnnotation.getClassFromAnn(Components.class);
            for (Class classes : classList) {
                Components components = (Components) classes.getAnnotation(Components.class);
                if (components != null && name.equals(components.name())) {
                    componentClass = classes;
                    componentClassMap.put(components.name(), componentClass);
                }
            }

        }

        if (componentClass == null) {
            throw new Exception("没有找到componest" + name);
        }
        Component autoDriver = (Component) componentClass.newInstance();
        //注入初始化信息

        Field beanMapField = componentClass.getField("componentBeanMap");
        if (initParamMap != null) {
            beanMap.putAll(initParamMap);//根据不同节点，传入对应初始化参数
        }
        beanMapField.set(autoDriver, beanMap);  //流程数据传入组件中执行


        return autoDriver;
    }

}
