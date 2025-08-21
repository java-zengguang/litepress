package io.github.java_zengguang.litepress.web.netty.reactor;

import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;
import io.github.java_zengguang.litepress.web.netty.sse.SSEDto;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.tinylog.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class StreamHttpResponseHandler extends BaseHttpResponseHandler {
    @Override
    public void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity) {
        if (responseEntity.result instanceof BlockingQueue<?> blockingQueue) {
            Thread.ofVirtual().start(() -> {
                Logger.info("SSE虚拟线程启动");
                Duration heartbeatInterval = Duration.ofSeconds(15);
                Instant lastHeartbeat = Instant.now();
                // 监听通道关闭（确保资源清理）
                ctx.channel().closeFuture().addListener(f -> {
                    blockingQueue.clear();
                    Logger.info("前端主动关闭，通道关闭，清理队列");
                });

                try {
                    // 初始化SSE响应
                    FullHttpResponse response = new DefaultFullHttpResponse(
                            HttpVersion.HTTP_1_1,
                            HttpResponseStatus.OK
                    );
                    response.headers()
                            .set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=UTF-8")
                            .set(HttpHeaderNames.CACHE_CONTROL, "no-cache")
                            .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

                    // 发送头信息并检查是否成功
                    ctx.writeAndFlush(response).addListener(future -> {
                        if (!future.isSuccess()) {
                            Logger.error("SSE头信息发送失败");
                        }
                    });

                    // 处理数据流
                    String flag = "keep";
                    while (!"stop".equals(flag)) {
                        try {
                            if (Duration.between(lastHeartbeat, Instant.now()).compareTo(heartbeatInterval) > 0) {
                             //   ctx.writeAndFlush(Unpooled.copiedBuffer("event: keepalive\n\n".getBytes()));
                                lastHeartbeat = Instant.now();
                            }

                            // 非阻塞获取数据
                            Object object = blockingQueue.poll(1, TimeUnit.SECONDS);
                            if (object == null) continue;

                            if (object instanceof SSEDto data) {
                                flag = data.flag;
                                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                HttpResponseStatus.OK,Unpooled.copiedBuffer(data.toSseFormat().getBytes())))
                                        .addListener(future -> {
                                            if (!future.isSuccess()) {
                                                Logger.error("写入失败: {}", data.flag);
                                            }
                                        });
                            }
                        } catch (InterruptedException e) {
                            Logger.info("SSE虚拟线程被中断，正常退出");
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } catch (Exception e) {
                    Logger.error("SSE处理异常: ", e);
                } finally {
                    Logger.info("SSE虚拟线程退出");
                    if (ctx.channel().isActive()) {
                        ctx.close().addListener((future)->{
                            if (future.isSuccess()) {
                                Logger.info("服务端成功关闭连接");
                            } else {
                                Logger.error("服务端关闭连接失败", future.cause());
                            }
                        });
                    }
                }
            });
        }
    }
}