package io.github.java_zengguang.litepress.web.netty.reactor;

import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.web.entity.CookieEntity;
import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.github.java_zengguang.litepress.web.entity.MVCOption;
import io.github.java_zengguang.litepress.web.enums.SceneType;
import io.github.java_zengguang.litepress.web.netty.adapter.HttpNettyControllerAdapter;
import io.github.java_zengguang.litepress.web.netty.sse.SSE2NettyManager;
import io.github.java_zengguang.litepress.web.netty.sse.SSEManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.tinylog.Logger;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;

// 自定义请求处理器
public class SimpleHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final HttpNettyControllerAdapter controllerAdapter = HttpNettyControllerAdapter.getInstance();


    public Map<String, List<String>> getHeaders(FullHttpRequest request) {
        // 使用 LinkedHashMap 保持插入顺序
        Map<String, List<String>> headersMap = new LinkedHashMap<>();
        // 获取请求的所有头
        request.headers().forEach((header) -> {
            // 如果这个头已经存在于 map 中，则获取其列表并添加新值
            // 否则创建一个新的列表并将值添加进去
            headersMap.computeIfAbsent(header.getKey(), k -> new ArrayList<>()).add(header.getValue());
        });

        return headersMap;
    }


    public List<CookieEntity> getCookies(FullHttpRequest request) {
        // 创建一个Map来存储Cookies
        List<CookieEntity> cookies = new ArrayList<>();

        // 获取请求头中的Cookie字符串
        String cookieHeader = request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieHeader != null && !cookieHeader.isEmpty()) {
            // 使用ServerCookieDecoder来解析Cookie字符串
            Set<io.netty.handler.codec.http.cookie.Cookie> decodedCookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
            for (Cookie cookie : decodedCookies) {
                // 将每个Cookie添加到Map中
                CookieEntity cookieEntity = new CookieEntity(cookie.name(), cookie.value());
                cookieEntity.domain = cookie.domain();
                cookieEntity.path = cookie.path();
                cookieEntity.maxAge = cookie.maxAge();
                cookieEntity.secure = cookie.isSecure();
                cookieEntity.httpOnly = cookie.isHttpOnly();
                cookies.add(cookieEntity);
            }
        }

        return cookies;
    }


    public HttpRequestEntity transHttpRequestEntity(FullHttpRequest request) {
        String requestURI = request.uri();
        HttpRequestEntity httpRequestEntity = new HttpRequestEntity();
        httpRequestEntity.url = requestURI;
        httpRequestEntity.contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        httpRequestEntity.methodType = request.method().name();
        httpRequestEntity.headers = getHeaders(request);
        httpRequestEntity.cookies = getCookies(request);

        QueryStringDecoder decoder = new QueryStringDecoder(httpRequestEntity.url);
        httpRequestEntity.path = decoder.path();
        Map<String, List<String>> uriAttributes = decoder.parameters();
        if (uriAttributes != null && !uriAttributes.isEmpty()) {
            uriAttributes.forEach((key, values) -> {
                //这里做了特殊处理，所有的摒弃一个字段对应多个值，只取第一个
                httpRequestEntity.paramMap.put(key, values.getFirst());
            });
        }


        if (HttpMethod.POST.equals(request.method()) && httpRequestEntity.contentType != null) {
            if (httpRequestEntity.contentType.equals("application/json")) {
                httpRequestEntity.sceneType = SceneType.JSON.name();
                httpRequestEntity.paramMap.put("body", request.content().toString(CharsetUtil.UTF_8));
            } else if (httpRequestEntity.contentType.startsWith("multipart/form-data")) {
                httpRequestEntity.sceneType = SceneType.FORM.name();
                httpRequestEntity.paramMap.putAll(readBody(request));
            } else if (httpRequestEntity.contentType.equals("application/x-www-form-urlencoded;charset=UTF-8")) {
                decoder = new QueryStringDecoder(request.content().toString(CharsetUtil.UTF_8), false);
                uriAttributes = decoder.parameters();
                if (uriAttributes != null && !uriAttributes.isEmpty()) {
                    uriAttributes.forEach((key, values) -> {
                        //这里做了特殊处理，所有的摒弃一个字段对应多个值，只取第一个
                        httpRequestEntity.paramMap.put(key, values.getFirst());
                    });
                }
            }
        }


        return httpRequestEntity;
    }

    private Map<String, Object> readBody(FullHttpRequest request) {
        MVCOption mvcOption = (MVCOption) Config.getConfig("MVCOption");
        Map<String, Object> paramMap = new HashMap<>();
        List<File> files = new ArrayList<>();
        if (request.headers().get(HttpHeaderNames.CONTENT_TYPE).startsWith("multipart/form-data")) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            try {
                while (decoder.hasNext()) {
                    InterfaceHttpData data = decoder.next();
                    if (data != null) {
                        switch (data.getHttpDataType()) {
                            case Attribute:
                                Attribute attribute = (Attribute) data;
                                paramMap.put(attribute.getName(), attribute.getValue());
                                break;
                            case FileUpload:
                                FileUpload fileUpload = (FileUpload) data;
                                if (fileUpload.isCompleted() && !fileUpload.getFilename().isEmpty()) {
                                    // 降级：读取字节数据
                                    ByteBuf byteBuf = fileUpload.getByteBuf();
                                    byte[] bytes = new byte[byteBuf.readableBytes()];
                                    byteBuf.readBytes(bytes);
                                    File file = new File(mvcOption.temporaryFilePath, fileUpload.getFilename());
                                    Files.write(Paths.get(mvcOption.temporaryFilePath + fileUpload.getFilename()), bytes);
                                    files.add(file);
                                }
                                break;
                        }
                    }
                }
                paramMap.put("files", files);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                // 释放资源
                decoder.destroy();
            }
        } else {
            Logger.info("Not a multipart POST request.");
        }

        return paramMap;
    }



    // Reactor 示例 - 与 Spring 生态完美集成
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        HttpRequestEntity httpRequestEntity = transHttpRequestEntity(msg);
        Mono.fromCallable(() -> controllerAdapter.dealHttpRequest(httpRequestEntity))
                .subscribeOn(Schedulers.boundedElastic()) // 内置弹性线程池
                .doOnError(error -> Logger.error("处理失败: {}", httpRequestEntity.path, error))
                .retryWhen(Retry.backoff(0, Duration.ofSeconds(1))) // 内置重试机制
                .subscribe(responseEntity -> ctx.executor().execute(() -> {
                    try {
                        if (responseEntity.result instanceof BlockingQueue) {
                            HttpResponseHandler httpResponseHandler = new StreamHttpResponseHandler();
                            httpResponseHandler.dealHttpResponse(ctx, responseEntity);
                        } else {
                            HttpResponseHandler httpResponseHandler = new SimpleHttpResponseHandler();
                            httpResponseHandler.dealHttpResponse(ctx, responseEntity);
                        }
                    } catch (Exception e) {
                        Logger.error(e);
                    }
                }), error -> ctx.executor().execute(() -> {
                    Logger.error(error);
                }));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 发生异常时关闭连接
        Logger.error(cause, "netty网络异常！");
        ctx.close();
    }
}