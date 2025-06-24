package io.github.java_zengguang.litepress.web.netty.reactor;

import io.github.java_zengguang.litepress.network.common.service.BaseKeepService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.*;

public class ReactorWebService extends BaseKeepService {

    public ReactorWebService(int port) {
        super(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch)  {
                ch.pipeline().addLast(new HttpServerCodec()); // (5)
                ch.pipeline().addLast(new HttpObjectAggregator(500*1024*1024)); // (6)
                ch.pipeline().addLast(new SimpleHttpRequestHandler()); // (7)
            }
        }, port);
    }

}
