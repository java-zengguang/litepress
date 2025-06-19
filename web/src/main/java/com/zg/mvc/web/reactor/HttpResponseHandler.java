package com.zg.mvc.web.reactor;

import com.zg.mvc.entity.HttpResponseEntity;
import io.netty.channel.ChannelHandlerContext;

public interface HttpResponseHandler {
    void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity ) throws InterruptedException;
}
