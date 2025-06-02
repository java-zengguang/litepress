package com.zg.litepress.web.netty.reactor;

import com.zg.litepress.web.entity.HttpResponseEntity;
import io.netty.channel.ChannelHandlerContext;

public interface HttpResponseHandler {
    void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity ) throws InterruptedException;
}
