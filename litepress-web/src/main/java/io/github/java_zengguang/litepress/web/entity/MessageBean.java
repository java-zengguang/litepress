package io.github.java_zengguang.litepress.web.entity;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;


public class MessageBean<T> extends MainModel {
    public String message;
    public boolean success;
    public T object;

    public MessageBean() {
    }

    public MessageBean(String message, boolean success, T object) {
        this.message = message;
        this.success = success;
        this.object = object;
    }

    /**
     * 成功（无数据）。
     */
    public static <T> MessageBean<T> ok() {
        return new MessageBean<>("ok", true, null);
    }

    /**
     * 成功（带数据）。
     */
    public static <T> MessageBean<T> ok(T object) {
        return new MessageBean<>("ok", true, object);
    }

    /**
     * 成功（带消息与数据）。
     */
    public static <T> MessageBean<T> ok(String message, T object) {
        return new MessageBean<>(message, true, object);
    }

    /**
     * 失败（默认消息）。
     */
    public static <T> MessageBean<T> error() {
        return new MessageBean<>("error", false, null);
    }

    /**
     * 失败（带错误消息）。
     */
    public static <T> MessageBean<T> error(String message) {
        return new MessageBean<>(message, false, null);
    }

    /**
     * 失败（带错误消息与数据）。
     */
    public static <T> MessageBean<T> error(String message, T object) {
        return new MessageBean<>(message, false, object);
    }
}