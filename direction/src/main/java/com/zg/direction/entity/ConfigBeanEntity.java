package com.zg.direction.entity;

import com.zg.common.bean.entity.MainModel;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ConfigBeanEntity extends MainModel {
    public String beanName;
    public String beanType;
    public String beanConfigProperties;
    public Object obj;  //bean对象


}
