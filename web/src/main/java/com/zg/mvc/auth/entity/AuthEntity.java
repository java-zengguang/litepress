package com.zg.mvc.auth.entity;

import com.zg.common.bean.entity.MainModel;

import java.util.Map;

public class AuthEntity extends MainModel {
    public String safeToken;
    public long timestamp;
    public Map<String, Object> userInfo;
}
