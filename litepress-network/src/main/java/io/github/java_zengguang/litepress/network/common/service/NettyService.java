package io.github.java_zengguang.litepress.network.common.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.tinylog.Logger;

public class NettyService {

    private final int bossThreads;
    private final int workerThreads;
    private final int port;
    private final ChannelInitializer channelInitializer;
    private final LogLevel logLevel;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    private NettyService(Builder builder) {
        this.bossThreads = builder.bossThreads;
        this.workerThreads = builder.workerThreads;
        this.port = builder.port;
        this.channelInitializer = builder.channelInitializer;
        this.logLevel = builder.logLevel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void start() {
        Logger.info("创建netty服务器");

        bossGroup = new NioEventLoopGroup(bossThreads);
        workerGroup = new NioEventLoopGroup(workerThreads);

        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(logLevel))
                    .childHandler(channelInitializer);

            ChannelFuture future = bootstrap.bind(port).sync();
            if (future.isSuccess()) {
                Logger.info("server start success... port: " + port + ", main work thread: "
                        + Thread.currentThread().getName());
            }

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Logger.error(e);
            Thread.currentThread().interrupt();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public static class Builder {
        private int bossThreads = 2;
        private int workerThreads = 32;
        private int port = 8080;
        private ChannelInitializer channelInitializer;
        private LogLevel logLevel = LogLevel.INFO;

        public Builder bossThreads(int bossThreads) {
            if (bossThreads <= 0) {
                throw new IllegalArgumentException("bossThreads must be positive");
            }
            this.bossThreads = bossThreads;
            return this;
        }

        public Builder workerThreads(int workerThreads) {
            if (workerThreads <= 0) {
                throw new IllegalArgumentException("workerThreads must be positive");
            }
            this.workerThreads = workerThreads;
            return this;
        }

        public Builder port(int port) {
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("port must be between 1 and 65535");
            }
            this.port = port;
            return this;
        }

        public Builder channelInitializer(ChannelInitializer channelInitializer) {
            if (channelInitializer == null) {
                throw new IllegalArgumentException("channelInitializer cannot be null");
            }
            this.channelInitializer = channelInitializer;
            return this;
        }

        public Builder logLevel(LogLevel logLevel) {
            if (logLevel == null) {
                throw new IllegalArgumentException("logLevel cannot be null");
            }
            this.logLevel = logLevel;
            return this;
        }

        public NettyService build() {
            if (channelInitializer == null) {
                throw new IllegalStateException("channelInitializer must be set");
            }
            return new NettyService(this);
        }
    }
}