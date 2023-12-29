package com.zg.network.common.service;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.tinylog.Logger;



public abstract class BaseKeepServiceHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Logger.info("收到请求："+msg);
        sendMsg(ctx.channel(), (String) msg);
    }


    public abstract void sendMsg(Channel channel, String msg) throws Exception;

}
