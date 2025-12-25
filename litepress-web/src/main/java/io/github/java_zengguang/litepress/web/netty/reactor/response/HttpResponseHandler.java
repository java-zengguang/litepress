package io.github.java_zengguang.litepress.web.netty.reactor.response;

import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;
import io.netty.channel.ChannelHandlerContext;

public interface HttpResponseHandler {
    void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity );
}
