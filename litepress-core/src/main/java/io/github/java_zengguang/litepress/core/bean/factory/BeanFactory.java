package io.github.java_zengguang.litepress.core.bean.factory;

import io.github.java_zengguang.litepress.core.password.PassWordUtil;
import io.github.java_zengguang.litepress.core.util.reflect.TransEntityTypeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class BeanFactory {

    private String config;

    public BeanFactory(String config) {
        this.config = config;
    }

    private  Element getRootElement() {
        try (InputStream inputStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8))) {
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            return document.getRootElement();
        } catch (Exception e) {
            Logger.error(e, "读取配置文件失败");
        }
        return null;
    }

    public  Map<String, Object> createAllBeans() {
        Map<String, Object> beanMaps = new HashMap<>();
        Map<String, Map< String,Field>> refMaps = new HashMap<>();
        try {
            Element root =getRootElement();
            for (Object beanO : root.elements("bean")) {
                Element bean = (Element) beanO;
                String beanName = bean.attributeValue("id");
                String beanClassName = bean.attributeValue("class");
                Object o = Class.forName(beanClassName).getDeclaredConstructor().newInstance();
                Map< String,Field > fieldRefMap = new HashMap();
                for (Object propertyO : bean.elements("property")) {

                    Element property = (Element) propertyO;
                    String name = property.attributeValue("name");
                    Field field = Class.forName(beanClassName).getField(name);
                    field.setAccessible(true);
                    if (property.attributeValue("type") != null) {
                        String value = property.getStringValue();
                        //对密码做个加密
                        if ("password".equals(name) && property.attributeValue("encryption") == null) {
                            value = PassWordUtil.decrypt(value);
                        }
                        field.set(o, TransEntityTypeUtils.translateType(value, field.getType().getSimpleName()));
                    }
                    if (property.attributeValue("ref") != null) {
                        //  field.set(o, BeanFactory.createBean(property.attributeValue("ref")));
                        fieldRefMap.put( property.attributeValue("ref"),field);
                    }
                }
                //    o = createProxy(o);   //添加代理
                if (!fieldRefMap.isEmpty()) {
                    refMaps.put(beanName, fieldRefMap);
                }
                beanMaps.put(beanName, o);
            }

            //第二部分处理ref引用注入
            refMaps.forEach((beanName,fieldObjectMap)-> {
                Object beanObject=beanMaps.get(beanName);
                fieldObjectMap.forEach((targetBeanName,field)->{
                   Object targetObject= beanMaps.get(targetBeanName);
                    try {
                        field.set(beanObject,targetObject);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            beanMaps.replaceAll((key, value) -> {
                try {
                    return createProxy(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


        } catch (Exception e) {
            Logger.error(e, "读取配置文件失败");
        }
        return beanMaps;
    }



    public  Object createProxy(Object target) throws NoSuchMethodException, SecurityException, ClassNotFoundException, DocumentException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String pName = target.getClass().getPackage().getName();
        Element root = getRootElement();
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
