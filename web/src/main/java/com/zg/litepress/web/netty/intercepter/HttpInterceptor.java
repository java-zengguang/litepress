package com.zg.litepress.web.netty.intercepter;

import com.zg.litepress.web.entity.HttpRequestEntity;
import com.zg.litepress.web.entity.HttpResponseEntity;



public interface HttpInterceptor {
    boolean doInvoke(HttpRequestEntity request, HttpResponseEntity response) ;
}
