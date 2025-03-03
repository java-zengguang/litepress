package com.zg.mvc.web.sse;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.tinylog.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class SSE2NettyManager extends BaseSSEManager implements SSEManager {
    private static final ConcurrentHashMap<String, ChannelHandlerContext> userChannels = new ConcurrentHashMap<>();   //客户端ID与链接上下文缓存
    private static final ConcurrentHashMap<ChannelHandlerContext, AtomicInteger> heartbeatTimeMap = new ConcurrentHashMap<>();  //心跳次数
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static SSE2NettyManager sseHandle = null;

    public static SSE2NettyManager getInstance() {
        if (sseHandle == null) {
            sseHandle = new SSE2NettyManager();
        }
        return sseHandle;
    }

    private SSE2NettyManager() {
        try {
            init();
        } catch (InterruptedException | MQClientException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws InterruptedException, MQClientException {
        scheduler.scheduleAtFixedRate(() ->
                        userChannels.forEach((key, ctx) -> {
                            AtomicInteger heartbeatTime = heartbeatTimeMap.getOrDefault(ctx, new AtomicInteger(-1));
                            if (heartbeatTime.get() < 0) { //心跳超时，关闭链接
                                Logger.info("心跳超时，自动关闭！");
                                ctx.close();
                            } else {
                                sendMsg(ctx, "data: heartbeat " + heartbeatTime);
                            }
                        })
                , 0, 30, TimeUnit.SECONDS);
    }


    public void sendSSE(SSEDto sseDto) {
        if (userChannels.containsKey(sseDto.clientId)) {
            ChannelHandlerContext ctx = userChannels.get(sseDto.clientId);
            sendMsg(ctx, sseDto.toSseFormat());
        } else {
            Logger.info("未找到链接！");
        }
    }

    public void createSSE(ChannelHandlerContext ctx, String key) {
        ChannelHandlerContext oldCtx = userChannels.get(key);
        if (oldCtx != null) {
            Logger.info("事件通道更新，关闭老通道！");
            oldCtx.close();
        }
        userChannels.put(key, ctx);
        // 注册关闭处理器以清理过期连接
        ctx.channel().closeFuture().addListener(future -> {
            Logger.info("关闭链接，清理缓存监听！");
            userChannels.remove(key, ctx);
            heartbeatTimeMap.remove(ctx);
        });
        sendMsg(ctx, "data: 消息通道创建！\r\n");

    }

    public void sendMsg(ChannelHandlerContext ctx, String msg) {

        // 响应设置为 SSE 格式
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK
                , Unpooled.copiedBuffer(msg.getBytes()));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.writeAndFlush(response).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                Logger.info("写入数据成功");
                heartbeatTimeMap.put(ctx, new AtomicInteger(3));  //每次成功都更新心跳
            } else {
                Logger.info("写入数据失败");
                heartbeatTimeMap.getOrDefault(ctx, new AtomicInteger(0)).decrementAndGet();  //每次失败计数器减一
            }
        });
    }

}
