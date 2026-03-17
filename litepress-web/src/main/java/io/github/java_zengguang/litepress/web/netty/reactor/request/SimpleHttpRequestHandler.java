package io.github.java_zengguang.litepress.web.netty.reactor.request;

import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 简单 HTTP 请求处理器。
 * 处理普通 HTTP 请求，通过组合方式复用 AbstractHttpRequestHandler 的功能。
 */
public class SimpleHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final AbstractHttpRequestHandler helper = new AbstractHttpRequestHandler() {};

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        HttpRequestEntity httpRequestEntity = helper.transHttpRequestEntity(msg);
        // 使用异步方式处理请求，不阻塞 Netty I/O 线程
        helper.processRequestAsync(ctx, httpRequestEntity);
    }
}
