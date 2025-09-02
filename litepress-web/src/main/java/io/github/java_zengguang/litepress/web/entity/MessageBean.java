package io.github.java_zengguang.litepress.web.entity;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;
import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;

/**
 * Created by Administrator on 2019/2/12 0012.
 */
public class MessageBean<T> extends MainModel {
    public String message;
    public boolean success;
    public T object;

    public PageEntity pageEntity;

    public MessageBean() {
    }

    public MessageBean(String message, boolean success, T object) {
        this.message = message;
        this.success = success;
        this.object = object;
    }

    public MessageBean(String message, boolean success, T object, PageEntity pageEntity) {
        this.message = message;
        this.success = success;
        this.object = object;
        this.pageEntity = pageEntity;
    }
}
