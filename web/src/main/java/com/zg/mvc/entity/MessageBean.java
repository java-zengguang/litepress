package com.zg.mvc.entity;

import com.zg.common.bean.entity.MainModel;

/**
 * Created by Administrator on 2019/2/12 0012.
 */
public class MessageBean extends MainModel {
    public String message;
    public boolean success;
    public Object object;

    public MessageBean(String message, boolean success, Object object) {
        this.message = message;
        this.success = success;
        this.object = object;
    }
}
