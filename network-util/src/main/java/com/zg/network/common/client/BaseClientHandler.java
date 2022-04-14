package com.zg.network.common.client;

import com.zg.network.common.MessgeReceivedListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public abstract class BaseClientHandler<T> extends SimpleChannelInboundHandler<T> {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    protected abstract void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception;

    public abstract void remove(MessgeReceivedListener messgeReceivedListener);

    public abstract void addMessgeReceivedListener(MessgeReceivedListener messgeReceivedListener);
}
