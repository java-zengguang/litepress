package com.zg.litepress.web.netty.sse;



import io.netty.channel.ChannelHandlerContext;


//利用事件总线绑定sse事件
public interface SSEManager {

    void sendSSE(SSEDto sseDto);

    void createSSE(ChannelHandlerContext ctx, String key);


}
