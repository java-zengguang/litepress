package io.github.java_zengguang.litepress.web.servlet.adapter;

import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;

public interface ControllerAdapter {

    HttpResponseEntity dealHttpRequest(HttpRequestEntity requestEntity) throws Exception;

}
