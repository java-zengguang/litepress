package com.zg.mvc.entity;

import com.zg.common.bean.entity.MainModel;

public class AuthConfig extends MainModel {

    public String perturbation = "";  //认证私钥
    public long accessTokenExpireTime = 40 * 60 * 1000;  //过期时间

    public long renewalTokenExpireTime = 10 * 60 * 1000;  //过期时间


}
