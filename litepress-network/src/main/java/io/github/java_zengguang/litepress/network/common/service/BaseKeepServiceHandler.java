package io.github.java_zengguang.litepress.network.common.service;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.tinylog.Logger;



public abstract class BaseKeepServiceHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Logger.debug("收到请求："+msg);
        sendMsg(ctx.channel(), (String) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理逻辑
        cause.printStackTrace();
        // 关闭Channel
        ctx.close();
    }

    public abstract void sendMsg(Channel channel, String msg) throws Exception;

}
