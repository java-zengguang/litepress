package io.github.java_zengguang.litepress.direction.server;

import io.github.java_zengguang.litepress.network.common.service.NettyService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ProviderKeepService {
    private final StringDecoder DECODER = new StringDecoder();
    private final StringEncoder ENCODER = new StringEncoder();
    private final NettyService nettyService;
    public static ProviderKeepService providerKeepService;

    public synchronized static ProviderKeepService getInstance(Integer port){
        if(providerKeepService==null){
            providerKeepService=new ProviderKeepService(port);
        }
        return providerKeepService;
    }



    private ProviderKeepService(int port) {
        ChannelInitializer channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                //获取管道
                ChannelPipeline pipe = socketChannel.pipeline();
                // Add the text line codec combination first,
                pipe.addLast(new DelimiterBasedFrameDecoder(1000 * 1000 * 1024, Delimiters.lineDelimiter()));
                // the encoder and decoder are static as these are sharable
                //字符串编码器
                pipe.addLast(DECODER);
                //字符串解码器
                pipe.addLast(ENCODER);
                //业务处理类
                pipe.addLast(new ProviderServiceHandler());
            }
        };
        nettyService = NettyService.builder().port(port).channelInitializer(channelInitializer).build();
    }

    public void doMain() {
        nettyService.start();
    }

}
