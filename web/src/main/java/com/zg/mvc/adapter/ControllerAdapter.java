package com.zg.mvc.adapter;

import com.zg.mvc.entity.HttpRequestEntity;
import com.zg.mvc.entity.HttpResponseEntity;

public interface ControllerAdapter {

    HttpResponseEntity dealHttpRequest(HttpRequestEntity requestEntity) throws Exception;

}
