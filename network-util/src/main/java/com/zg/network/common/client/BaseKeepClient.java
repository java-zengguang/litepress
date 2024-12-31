package com.zg.network.common.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zg.common.util.reflect.JsonUtil;
import com.zg.network.common.cache.BaseMessageCache;
import com.zg.network.entity.BaseRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import org.tinylog.Logger;

import java.net.InetSocketAddress;


public class BaseKeepClient {
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    private final int threadSize = 20;  //并发数量
    private Integer port;
    private String host;

    private ChannelPool channelPool;

    private Class agreementClass;  //协议
    EventLoopGroup workerGroup;
    private static Table<String, Integer, BaseKeepClient> clientTable = HashBasedTable.create();  //host port client


    private BaseKeepClient(String host, Integer port, Class agreementClass) {
        this.host = host;
        this.port = port;
        this.agreementClass = agreementClass;
        init();

    }

    private void init(){
        workerGroup = new NioEventLoopGroup(threadSize);
        // 创建 ChannelPool
        channelPool = new FixedChannelPool(new Bootstrap()
                .remoteAddress(new InetSocketAddress(host, port))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .group(workerGroup)
                , new AbstractChannelPoolHandler() {

            @Override
            public void channelCreated(Channel ch) {
                Logger.debug("Channel create.");
                // 初始化处理器
                ChannelPipeline pipe = ch.pipeline();
                pipe.addLast(new DelimiterBasedFrameDecoder(1000 * 1000 * 1024, Delimiters.lineDelimiter()));
                pipe.addLast(DECODER);
                pipe.addLast(ENCODER);
                pipe.addLast(new BaseKeepClientHandler(agreementClass));
            }
        }, 10);
    }

    public void sendRequest(BaseRequest request) throws   InterruptedException {
        String json = JsonUtil.obj2String(request.message);
        Future<Channel> future = channelPool.acquire().await();
        if(future.isSuccess()){
            Channel channel=future.getNow();
            channel.writeAndFlush(json + "\r\n");
            Logger.debug("发送请求："+json);
            BaseMessageCache.addWaitingRequest(request);
            channelPool.release(channel);
        }

    }
    public void close() {
        Logger.debug("close  client: " +host+":"+port);
        //优雅的关闭工作线程
        workerGroup.shutdownGracefully();
        clientTable.remove(host, port);
    }



    public static BaseKeepClient getInstance(String host, int port, Class agreementClass) {
        BaseKeepClient baseKeepClient = clientTable.get(host, port);
        if (baseKeepClient == null) {
            baseKeepClient = new BaseKeepClient(host, port, agreementClass);
            clientTable.put(host, port, baseKeepClient);
        }
        return baseKeepClient;
    }

    public static void close(String host, int port) {
        BaseKeepClient baseKeepClient = clientTable.remove(host, port);
        if (baseKeepClient != null) {
            baseKeepClient.close();
        }
    }

}
