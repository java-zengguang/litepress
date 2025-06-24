package io.github.java_zengguang.litepress.web.entity;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;
import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;

/**
 * Created by Administrator on 2019/2/12 0012.
 */
public class MessageBean extends MainModel {
    public String message;
    public boolean success;
    public Object object;

    public PageEntity pageEntity;

    public MessageBean(String message, boolean success, Object object) {
        this.message = message;
        this.success = success;
        this.object = object;
    }

    public MessageBean(String message, boolean success, Object object, PageEntity pageEntity) {
        this.message = message;
        this.success = success;
        this.object = object;
        this.pageEntity = pageEntity;
    }
}
