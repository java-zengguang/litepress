package io.github.java_zengguang.litepress.web.netty.reactor.response;

import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;

import java.util.concurrent.BlockingQueue;

public class HttpResponseHandlerFactory {
    public static HttpResponseHandler getHandler(HttpResponseEntity responseEntity) {
        if (responseEntity.result instanceof BlockingQueue) {
            return new StreamHttpResponseHandler();
        } else {
            return new SimpleHttpResponseHandler();
        }
    }
}
