package io.github.java_zengguang.litepress.web.netty.intercepter;

import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;



public interface HttpInterceptor {
    boolean doInvoke(HttpRequestEntity request, HttpResponseEntity response) ;
}
