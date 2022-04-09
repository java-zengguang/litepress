package com.zg.network.common.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public abstract class BaseService implements Runnable {

    private Logger logger = LoggerFactory.getLogger(BaseService.class);
    private final StringDecoder DECODER = new StringDecoder();
    private final StringEncoder ENCODER = new StringEncoder();
    private NioEventLoopGroup bossGroup = null;
    private NioEventLoopGroup workerGroup = null;
    private int port;
    private BaseServiceHandler baseServiceHandler;

    public BaseService(BaseServiceHandler<String> baseServiceHandler, int port) {

        this.baseServiceHandler = baseServiceHandler;
        this.port = port;
    }

    public abstract void startHeartbeat();

    public void getConnectin() {

        //boss线程监听端口，worker线程负责数据读写
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        //辅助启动类
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            //设置线程池
            bootstrap.group(bossGroup, workerGroup);
            //设置socket工厂
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            //设置管道工厂
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //获取管道
                    ChannelPipeline pipe = socketChannel.pipeline();

                    // Add the text line codec combination first,
                    pipe.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    // the encoder and decoder are static as these are sharable
                    //字符串编码器
                    pipe.addLast(DECODER);
                    //字符串解码器
                    pipe.addLast(ENCODER);
                    //业务处理类
                    pipe.addLast(baseServiceHandler);
                }
            });

        /*    IMHeartbeatHandle IMHeartbeatHandle =new IMHeartbeatHandle(BaseChannelGroups.getChanelGroups());
            Thread t=new Thread(IMHeartbeatHandle);
            t.start();*/

            startHeartbeat();

            //绑定端口
            // Bind and start to accept incoming connections.
            ChannelFuture f = bootstrap.bind(port).sync();
            if (f.isSuccess()) {
                logger.info("server start success... port: " + port + ", main work thread: "
                        + Thread.currentThread().getId());
            }
            ////等待服务端监听端口关闭
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    @Override
    public void run() {
        if (port != 0) {
            getConnectin();
        } else {
            logger.info("没有定义端口");
        }
    }


}
