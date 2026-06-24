package io.github.java_zengguang.litepress.web.entity;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

public class AuthConfig extends MainModel {

    public String perturbation = "";  //认证私钥
    public long accessTokenExpireTime = 40 * 60 * 1000;  //过期时间

    public long renewalTokenExpireTime = 10 * 60 * 1000;  //过期时间


}
