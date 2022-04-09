package com.zg.bean.factory;

import com.zg.database.util.PassWordUtil;
import com.zg.util.io.FileUtils;
import com.zg.util.reflect.EntityUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.lang.reflect.*;


public class BeanFactory {

    public static Object createBean(String rootPath, String id) {
        Object o = null;
        Element root;
        try {
            root = FileUtils.getRootElement(rootPath, "BeanConfig.xml");

            for (Object beanO : root.elements("bean")) {
                Element bean = (Element) beanO;
                String beanName = bean.attributeValue("id");

                if (beanName.equals(id)) {
                    String beanClassName = bean.attributeValue("class");
                    o = Class.forName(beanClassName).newInstance();
                    for (Object propertyO : bean.elements("property")) {
                        Element property = (Element) propertyO;
                        String name = property.attributeValue("name");
                        Field field = Class.forName(beanClassName).getField(name);
                        field.setAccessible(true);
                        if (property.attributeValue("type") != null) {
                            EntityUtils.setField(field, o, property.getStringValue());
                        }
                        if (property.attributeValue("ref") != null) {
                            field.set(o, BeanFactory.createBean(property.attributeValue("ref")));
                        }
                    }
                }//
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            o = createProxy(o);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return o;

    }


    public static Object createBean(String id) {
        Object o = null;
        Element root;
        try {
            root = FileUtils.getRootElement("BeanConfig.xml");

            for (Object beanO : root.elements("bean")) {
                Element bean = (Element) beanO;
                String beanName = bean.attributeValue("id");

                if (beanName.equals(id)) {
                    String beanClassName = bean.attributeValue("class");
                    o = Class.forName(beanClassName).newInstance();
                    for (Object propertyO : bean.elements("property")) {
                        Element property = (Element) propertyO;
                        String name = property.attributeValue("name");
                        Field field = Class.forName(beanClassName).getField(name);
                        field.setAccessible(true);
                        if (property.attributeValue("type") != null) {
                            String value = property.getStringValue();
                            //对密码做个加密
                            if ("password".equals(name)) {
                                value = PassWordUtil.decrypt(value);

                            }
                            EntityUtils.setField(field, o, value);
                        }
                        if (property.attributeValue("ref") != null) {
                            field.set(o, BeanFactory.createBean(property.attributeValue("ref")));
                        }
                    }
                }//
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            o = createProxy(o);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return o;

    }


    public static Object createProxy(Object target) throws NoSuchMethodException, SecurityException, ClassNotFoundException, DocumentException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String pName = target.getClass().getPackage().getName();
        Element root = FileUtils.getRootElement("BeanConfig.xml");
        for (Object proxyO : root.elements("proxy")) {
            Element proxy = (Element) proxyO;
            String className = proxy.attributeValue("handler");
            if (pName.equals(proxy.attributeValue("package"))) {
                Constructor constructor = Class.forName(className).getConstructor(Object.class, String.class);
                InvocationHandler hand = (InvocationHandler) constructor.newInstance(target, proxy.attributeValue("method"));
                target = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), hand);
            }
        }
        return target;
    }

}
