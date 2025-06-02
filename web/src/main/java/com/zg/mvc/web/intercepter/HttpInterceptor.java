package com.zg.mvc.web.intercepter;

import com.zg.mvc.entity.HttpRequestEntity;
import com.zg.mvc.entity.HttpResponseEntity;



public interface HttpInterceptor {
    boolean doInvoke(HttpRequestEntity request, HttpResponseEntity response) ;
}
