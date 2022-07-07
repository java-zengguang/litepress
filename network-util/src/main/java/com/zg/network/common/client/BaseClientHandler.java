package com.zg.network.common.client;

import com.zg.network.common.MessgeReceivedListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public abstract class BaseClientHandler<T> extends SimpleChannelInboundHandler<T> {

    public final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    public List<MessgeReceivedListener> messgeReceivedListeners=new ArrayList<>();

    @Override
    protected  void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception{
        for(MessgeReceivedListener messgeReceivedListener:messgeReceivedListeners){
            messgeReceivedListener.onMessageReceived(msg);
        }
    }

    public  void remove(MessgeReceivedListener messgeReceivedListener){
        messgeReceivedListeners.remove(messgeReceivedListener);
    }

    public  void addMessgeReceivedListener(MessgeReceivedListener messgeReceivedListener){
        messgeReceivedListeners.add(messgeReceivedListener);
    }
}
