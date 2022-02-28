package com.zg.network.common.service;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
@ChannelHandler.Sharable
public abstract class BaseServiceHandler<T> extends SimpleChannelInboundHandler<T> {


    @Override
    protected abstract void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception;

/*    public abstract void remove(MessgeReceivedListener messgeReceivedListener) ;

    public abstract void addMessgeReceivedListener(MessgeReceivedListener messgeReceivedListener) ;*/
}
