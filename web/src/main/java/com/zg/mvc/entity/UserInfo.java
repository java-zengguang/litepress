package com.zg.mvc.entity;

import com.zg.common.bean.entity.MainModel;

public class UserInfo  extends MainModel {
    public String userName;
    public String userCode;
    public String safeToken;
    public String state;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getSafeToken() {
        return safeToken;
    }

    public void setSafeToken(String safeToken) {
        this.safeToken = safeToken;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
