package com.zg.litepress.web.entity;

import com.zg.litepress.core.bean.entity.MainModel;

import java.util.HashMap;
import java.util.Map;

public class RequestParamEntity extends MainModel {
    public String name;
    public String type;
    public Map<String, String> formHeadMap = new HashMap<>();
    public Object value;
}
