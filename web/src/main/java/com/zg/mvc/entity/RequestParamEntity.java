package com.zg.mvc.entity;

import com.zg.common.bean.entity.MainModel;

import java.util.HashMap;
import java.util.Map;

public class RequestParamEntity extends MainModel {
    public String name;
    public String type;
    public  Map<String,String> formHeadMap=new HashMap<>();
    public Object value;
}
