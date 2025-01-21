package com.zg.network.common.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.tinylog.Logger;


public  class BaseKeepService{

    private final StringDecoder DECODER = new StringDecoder();
    private final StringEncoder ENCODER = new StringEncoder();
    private final int threadSize = 10;  //并发数量
    private NioEventLoopGroup bossGroup = null;
    private NioEventLoopGroup workerGroup = null;
    private final int port;
    private ChannelInitializer channelInitializer;


    public BaseKeepService(BaseKeepServiceHandler baseServiceHandler, int port) {
        this.port = port;
        this.channelInitializer=new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
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
                pipe.addLast(baseServiceHandler);
            }
        };

    }

    public BaseKeepService(ChannelInitializer channelInitializer, int port) {
        this.channelInitializer = channelInitializer;
        this.port = port;

    }

    public void doMain(){
        Logger.info("创建netty服务器");

        //boss线程监听端口，worker线程负责数据读写
        bossGroup = new NioEventLoopGroup(threadSize);
        workerGroup = new NioEventLoopGroup(threadSize);
        //辅助启动类
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            //设置线程池
            bootstrap.group(bossGroup, workerGroup);
            //设置socket工厂
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            //设置管道工厂
            bootstrap.childHandler(channelInitializer);

            //绑定端口
            // Bind and start to accept incoming connections.
            ChannelFuture f = bootstrap.bind(port).sync();
            if (f.isSuccess()) {
                Logger.info("server start success... port: " + port + ", main work thread: "
                        + Thread.currentThread().getId());
            }
            ////等待服务端监听端口关闭
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Logger.error(e);
            Thread.currentThread().interrupt();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }





}
