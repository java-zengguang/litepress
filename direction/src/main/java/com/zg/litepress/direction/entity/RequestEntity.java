package com.zg.litepress.direction.entity;

import com.zg.litepress.core.bean.entity.MainModel;

public class RequestEntity extends MainModel {

    public String requestID;
    public Object requestMsg;

    public Object responseMsg;
    public Thread blockThread; //阻塞线程
}
