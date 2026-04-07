package io.github.java_zengguang.litepress.web.netty.reactor.response;

import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;
import io.github.java_zengguang.litepress.web.netty.sse.SSEDto;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.tinylog.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSE 流式响应处理器。
 * 
 * 使用虚拟线程处理 SSE 数据流，支持：
 * - 阻塞队列消费
 * - 心跳保活
 * - 优雅关闭
 * 
 * 注意：SSE 业务方法应该快速返回 BlockingQueue，然后在其他线程中往队列放数据。
 * 如果业务方法同步往队列放数据，会阻塞 Reactor 线程池。
 */
public class StreamHttpResponseHandler extends BaseHttpResponseHandler {

    /**
     * 心跳间隔（秒）
     */
    private static final int HEARTBEAT_INTERVAL_SECONDS = 15;

    /**
     * 队列拉取超时（秒）
     */
    private static final int POLL_TIMEOUT_SECONDS = 1;

    @Override
    public void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity) {
        if (!(responseEntity.result instanceof BlockingQueue<?> blockingQueue)) {
            Logger.warn("SSE 响应结果不是 BlockingQueue 类型，无法进行流式输出");
            return;
        }

        // 使用原子标记确保资源只清理一次
        AtomicBoolean cleaned = new AtomicBoolean(false);

        // 启动虚拟线程处理 SSE 流
        Thread.ofVirtual().name("sse-stream-" + ctx.channel().id().asShortText()).start(() -> {
            Logger.info("SSE 虚拟线程启动，通道: {}", ctx.channel().id().asShortText());

            Instant lastHeartbeat = Instant.now();

            // 监听通道关闭事件
            ctx.channel().closeFuture().addListener(future -> {
                cleanupResources(blockingQueue, cleaned, "前端主动关闭连接");
            });

            try {
                // 发送 SSE 响应头
                if (!sendSSEHeaders(ctx)) {
                    return;
                }

                // 处理数据流
                String flag = "keep";
                while (!"stop".equals(flag) && ctx.channel().isActive()) {
                    try {
                        // 检查心跳
                        if (Duration.between(lastHeartbeat, Instant.now()).compareTo(Duration.ofSeconds(HEARTBEAT_INTERVAL_SECONDS)) > 0) {
          /*                  if (!sendHeartbeat(ctx)) {
                                break;
                            }*/
                            lastHeartbeat = Instant.now();
                        }

                        // 非阻塞获取数据
                        Object object = blockingQueue.poll(POLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        if (object == null) {
                            // 超时，继续循环检查心跳和通道状态
                            continue;
                        }

                        if (object instanceof SSEDto data) {
                            flag = data.flag;
                            if (!sendSSEData(ctx, data)) {
                                break;
                            }
                        } else if (object != null) {
                            Logger.warn("SSE 队列中包含非 SSEDto 类型数据: {}", object.getClass().getName());
                        }
                    } catch (InterruptedException e) {
                        Logger.info("SSE 虚拟线程被中断，正常退出");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                Logger.info("SSE 数据流结束，flag: {}, 通道活跃: {}", flag, ctx.channel().isActive());

            } catch (Exception e) {
                Logger.error(e, "SSE 处理异常");
            } finally {
                cleanupResources(blockingQueue, cleaned, "SSE 流结束");
                closeChannel(ctx);
            }
        });
    }

    /**
     * 发送 SSE 响应头
     */
    private boolean sendSSEHeaders(ChannelHandlerContext ctx) {
        if (!ctx.channel().isActive()) {
            return false;
        }

        HttpResponse response = new DefaultHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK
        );
        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=UTF-8")
                .set(HttpHeaderNames.CACHE_CONTROL, "no-cache")
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

        // 异步发送，不阻塞虚拟线程
        ctx.writeAndFlush(response);
        Logger.debug("SSE 响应头已发送");
        return true;
    }

    /**
     * 发送心跳注释（保持连接活跃）
     */
    private boolean sendHeartbeat(ChannelHandlerContext ctx) {
        if (!ctx.channel().isActive()) {
            return false;
        }
        // SSE 心跳使用注释格式：冒号开头的行会被浏览器忽略
        ctx.writeAndFlush(Unpooled.copiedBuffer(": heartbeat\n\n".getBytes()))
                .addListener(future -> {
                    if (!future.isSuccess()) {
                        Logger.warn("SSE 心跳发送失败: {}", future.cause().getMessage());
                    }
                });
        Logger.debug("SSE 心跳已发送");
        return true;
    }

    /**
     * 发送 SSE 数据
     */
    private boolean sendSSEData(ChannelHandlerContext ctx, SSEDto data) {
        if (!ctx.channel().isActive()) {
            return false;
        }

        DefaultHttpContent content = new DefaultHttpContent(
                Unpooled.copiedBuffer(data.toSseFormat().getBytes()));

        // 异步发送，不阻塞虚拟线程
        ctx.writeAndFlush(content).addListener(future -> {
            if (future.isSuccess()) {
                Logger.debug("SSE 数据已发送, event: {}, id: {}", data.event, data.id);
            } else {
                Logger.warn("SSE 数据发送失败: {}", future.cause().getMessage());
            }
        });
        return true;
    }

    /**
     * 清理资源
     */
    private void cleanupResources(BlockingQueue<?> queue, AtomicBoolean cleaned, String reason) {
        if (cleaned.compareAndSet(false, true)) {
            queue.clear();
            Logger.info("SSE 资源清理完成，原因: {}", reason);
        }
    }

    /**
     * 关闭通道
     */
    private void closeChannel(ChannelHandlerContext ctx) {
        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                    .addListener(ChannelFutureListener.CLOSE)
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            Logger.info("SSE 连接已关闭");
                        } else {
                            Logger.error("SSE 连接关闭失败", future.cause());
                        }
                    });
        }
        Logger.info("SSE 虚拟线程退出");
    }
}