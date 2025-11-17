package io.github.java_zengguang.litepress.web.netty.reactor;

import io.github.java_zengguang.litepress.network.common.service.NettyService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.*;

public class ReactorWebService {
   private final   NettyService nettyService;

   private static ReactorWebService reactorWebService;

   public synchronized static ReactorWebService getInstance(Integer port){
       if(reactorWebService==null){
           reactorWebService=new ReactorWebService(port);
       }
       return reactorWebService;
   }

    private ReactorWebService(int port) {
       ChannelInitializer channelInitializer=  new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch)  {
                ch.pipeline()
                        .addLast(new HttpServerCodec())
                        .addLast(new HttpObjectAggregator(500*1024*1024))
                        .addLast(new SimpleHttpRequestHandler()); // (5)
            }
        };
        nettyService = NettyService.builder()
                .port(port)
                .bossThreads(2)
                .workerThreads(32)
                .channelInitializer(channelInitializer).build();
    }

    public void doMain(){
       nettyService.start();
    }

}
