package io.github.java_zengguang.litepress.web.netty.reactor;

import io.github.java_zengguang.litepress.network.common.service.NettyService;
import io.github.java_zengguang.litepress.web.netty.reactor.request.SSEHttpRequestHandler;
import io.github.java_zengguang.litepress.web.netty.reactor.request.SimpleHttpRequestHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ReactorWebService {
    private final NettyService nettyService;

    private static ReactorWebService reactorWebService;

    public synchronized static ReactorWebService getInstance(Integer port) {
        if (reactorWebService == null) {
            reactorWebService = new ReactorWebService(port);
        }
        return reactorWebService;
    }

    private ReactorWebService(int port) {
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) {
                ChannelPipeline pipeline = ch.pipeline();

                // 纯http聚合实现，不支持SSE
                pipeline.addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(500 * 1024 * 1024))
                        .addLast(new SimpleHttpRequestHandler());
                // 兼容实现，支持SSE
                pipeline.addLast(new HttpServerCodec())
                        .addLast(new ChunkedWriteHandler())
                        .addLast(new SSEHttpRequestHandler());
            }
        };
        nettyService = NettyService.builder()
                .port(port)
                .bossThreads(2)
                .workerThreads(4)
                .channelInitializer(channelInitializer).build();
    }

    public void doMain() {
        nettyService.start();
    }

}
