package io.github.java_zengguang.litepress.web.netty.reactor.request;

import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.tinylog.Logger;

/**
 * 兼容 HTTP 和 SSE (Server-Sent Events) 请求的 Handler。
 * 支持通过 GET 或 POST 方式建立请求，并根据返回值类型自动选择响应方式：
 * - 如果 controller 返回的 HttpResponseEntity.result 是 BlockingQueue，则使用 SSE 流式响应
 * - 否则使用普通 HTTP 响应
 */
public class SSEHttpRequestHandler extends SimpleChannelInboundHandler<Object> {

    // 复用 AbstractHttpRequestHandler 的公共方法
    private final AbstractHttpRequestHandler helper = new AbstractHttpRequestHandler() {};

    // 用于聚合 POST 请求体的状态变量
    private HttpRequest currentRequest;
    private CompositeByteBuf contentBuffer;
    private HttpPostRequestDecoder multipartDecoder;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            // 处理 HTTP 请求头
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof HttpContent) {
            // 处理 POST 请求体（如果有）
            handleHttpContent(ctx, (HttpContent) msg);
        }
    }

    /**
     * 处理 HTTP 请求头。
     * 如果是 GET 请求，立即处理；如果是 POST 请求，初始化缓冲区等待 Body。
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        this.currentRequest = request;

        // 如果是 POST 请求，需要聚合 Body 内容
        if (HttpMethod.POST.equals(request.method())) {
            String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
            boolean isMultipart = contentType != null && contentType.startsWith("multipart/form-data");

            contentBuffer = Unpooled.compositeBuffer();
            if (isMultipart) {
                multipartDecoder = new HttpPostRequestDecoder(request);
            }
        } else {
            // GET 请求通常没有 Body，直接处理
            processRequest(ctx, Unpooled.EMPTY_BUFFER);
        }
    }

    /**
     * 处理 HTTP 请求体（针对 POST 请求）。
     */
    private void handleHttpContent(ChannelHandlerContext ctx, HttpContent content) throws Exception {
        // 仅当 POST 请求时处理内容聚合
        if (currentRequest == null || !HttpMethod.POST.equals(currentRequest.method())) {
            return;
        }

        // 聚合内容
        if (multipartDecoder != null) {
            multipartDecoder.offer(content);
        } else if (contentBuffer != null) {
            contentBuffer.addComponent(true, content.content().retain());
        }

        if (content instanceof LastHttpContent) {
            // 请求完整，开始处理
            try {
                processRequest(ctx, contentBuffer);
            } finally {
                cleanup();
            }
        }
    }

    /**
     * 处理请求逻辑：构建实体对象 -> 异步调用业务层 -> 使用回调处理响应。
     */
    private void processRequest(ChannelHandlerContext ctx, ByteBuf content) {
        if (currentRequest == null) {
            return;
        }

        // 1. 构建完整的 FullHttpRequest 用于解析参数
        FullHttpRequest fullRequest = new DefaultFullHttpRequest(
                currentRequest.protocolVersion(),
                currentRequest.method(),
                currentRequest.uri(),
                content != null ? content : Unpooled.EMPTY_BUFFER
        );
        fullRequest.headers().setAll(currentRequest.headers());

        // 2. 转换为业务实体
        HttpRequestEntity httpRequestEntity = helper.transHttpRequestEntity(fullRequest);

        // 3. 使用异步方式处理请求，不阻塞 Netty I/O 线程
        helper.processRequestAsync(ctx, httpRequestEntity);
    }

    private void cleanup() {
        if (contentBuffer != null) {
            contentBuffer.release();
            contentBuffer = null;
        }
        if (multipartDecoder != null) {
            multipartDecoder.destroy();
            multipartDecoder = null;
        }
        currentRequest = null;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        cleanup();
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Logger.error(cause, "SSEHttpRequestHandler异常");
        cleanup();
        ctx.close();
    }
}