package com.zg.mvc.web.sse;



import io.netty.channel.ChannelHandlerContext;


//利用事件总线绑定sse事件
public interface SSEHandler {

    void sendSSE(SSEDto sseDto);

    void createSSE(ChannelHandlerContext ctx, String key);


}
