package io.github.java_zengguang.litepress.web.netty.reactor;

import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;
import io.netty.channel.ChannelHandlerContext;

public interface HttpResponseHandler {
    void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity ) throws InterruptedException;
}
