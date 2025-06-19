package com.zg.litepress.router.entity;

import com.zg.litepress.core.bean.entity.MainModel;

import java.util.Objects;

public class RouterRegisterConfig extends MainModel {
    public String registerURL;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RouterRegisterConfig that = (RouterRegisterConfig) o;
        return Objects.equals(registerURL, that.registerURL);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(registerURL);
    }
}
