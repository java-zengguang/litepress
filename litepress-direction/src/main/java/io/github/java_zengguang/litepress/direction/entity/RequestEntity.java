package io.github.java_zengguang.litepress.direction.entity;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

public class RequestEntity extends MainModel {

    public String requestID;
    public Object requestMsg;

    public Object responseMsg;
    public Thread blockThread; //阻塞线程
}
