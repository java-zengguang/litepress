package com.zg.network.common.client;

import com.zg.network.common.MessgeReceivedListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2019/2/22 0022.
 */
public abstract class BaseClient implements Runnable {

    /**
     * String字符串解码器
     */
    private static final StringDecoder DECODER = new StringDecoder();
    /***
     * String字符串编码器
     */
    private static final StringEncoder ENCODER = new StringEncoder();
    private final int threadSize = 10;  //并发数量
    public int port;
    public String host;
    private BlockingQueue<Object> requests = new LinkedBlockingQueue<>();
    /**
     * 客户端业务处理Handler
     */
    private BaseClientHandler clientHandler;
    /**
     * 是否继续进行运行
     */
    private boolean run = true;


    public BaseClient() {

    }

    public BaseClient(BaseClientHandler<String> clientHandler, String host, int port) {

        initParam(clientHandler, host, port);
    }

    public void initParam(BaseClientHandler<String> clientHandler, String host, int port) {
        this.clientHandler = clientHandler;
        this.host = host;
        this.port = port;
    }

    /**
     * 添加发送请求Request
     *
     * @param request
     */
    public void addRequest(Object request) {
        try {
            requests.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void execute() {
        //工作线程
        EventLoopGroup workerGroup = new NioEventLoopGroup(threadSize);
        ExecutorService executorService = Executors.newCachedThreadPool();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //辅助启动类
                Bootstrap bootstrap = new Bootstrap(); // (1)
                try {

                    //设置线程池
                    bootstrap.group(workerGroup); // (2)
                    //设置socket工厂 不是ServerSocket而是Socket
                    bootstrap.channel(NioSocketChannel.class); // (3)
                    bootstrap.handler(new LoggingHandler(LogLevel.INFO));
                    //设置管道工厂
                    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipe = ch.pipeline();
                            // Add the text line codec combination first,
                            pipe.addLast(new DelimiterBasedFrameDecoder(1000 * 1000 * 1024, Delimiters.lineDelimiter()));
                            // the encoder and decoder are static as these are sharable
                            //字符串解码器
                            pipe.addLast(DECODER);
                            //字符串编码器
                            pipe.addLast(ENCODER);
                            //IM业务处理类
                            pipe.addLast(clientHandler);
                        }
                    });


                    // Start the client.
                    ChannelFuture f = bootstrap.connect(host, port).sync(); // (5)
                    Channel channel = f.channel();
                    ChannelFuture lastWriteFuture = null;
                    while (run) {
                        Object request = requests.take();

                        String json = resovleProtocol(request);
                        // Sends the received line to the server.
                        lastWriteFuture = channel.writeAndFlush(json + "\r\n");

                    }
                    // Wait until all messages are flushed before closing the channel.
                    if (lastWriteFuture != null) {
                        lastWriteFuture.sync();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    //优雅的关闭工作线程
                    workerGroup.shutdownGracefully();
                }

            }
        };

        for (int i = 0; i < threadSize; i++) {
            executorService.submit(runnable);
        }
    }


    public abstract String resovleProtocol(Object object) throws IllegalAccessException;

    public void run() {
        // host = "127.0.0.1";
        // port = 10000;
        execute();

    }

    /**
     * 增加消息监听接受接口
     *
     * @param messgeReceivedListener
     */
    public void addMessgeReceivedListener(MessgeReceivedListener messgeReceivedListener) {
        clientHandler.addMessgeReceivedListener(messgeReceivedListener);
    }

    /***
     *  移除消息监听接口
     * @param messgeReceivedListener
     */
    public void remove(MessgeReceivedListener messgeReceivedListener) {
        clientHandler.remove(messgeReceivedListener);
    }


}
