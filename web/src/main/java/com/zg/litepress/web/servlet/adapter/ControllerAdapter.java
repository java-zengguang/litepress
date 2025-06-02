package com.zg.litepress.web.servlet.adapter;

import com.zg.litepress.web.entity.HttpRequestEntity;
import com.zg.litepress.web.entity.HttpResponseEntity;

public interface ControllerAdapter {

    HttpResponseEntity dealHttpRequest(HttpRequestEntity requestEntity) throws Exception;

}
