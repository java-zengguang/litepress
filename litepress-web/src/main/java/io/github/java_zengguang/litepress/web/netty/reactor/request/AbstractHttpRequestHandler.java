package io.github.java_zengguang.litepress.web.netty.reactor.request;

import io.github.java_zengguang.litepress.core.error.BizException;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.web.entity.CookieEntity;
import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;
import io.github.java_zengguang.litepress.web.entity.MVCOption;
import io.github.java_zengguang.litepress.web.enums.SceneType;
import io.github.java_zengguang.litepress.web.netty.adapter.HttpNettyControllerAdapter;
import io.github.java_zengguang.litepress.web.netty.reactor.response.HttpResponseHandler;
import io.github.java_zengguang.litepress.web.netty.reactor.response.HttpResponseHandlerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * HTTP 请求处理器抽象基类。
 * 提取了 SimpleHttpRequestHandler 和 SSEHttpRequestHandler 的公共代码。
 * 使用 Project Reactor 实现响应式异步处理。
 */
public abstract class AbstractHttpRequestHandler {

    protected final HttpNettyControllerAdapter controllerAdapter = HttpNettyControllerAdapter.getInstance();

    protected final String tempDir;

    public AbstractHttpRequestHandler() {
        MVCOption mvcOption = (MVCOption) Config.getConfig("MVCOption");
        this.tempDir = mvcOption != null ? mvcOption.temporaryFilePath : "/tmp";
    }

    /**
     * 获取请求头
     */
    public Map<String, List<String>> getHeaders(FullHttpRequest request) {
        Map<String, List<String>> headersMap = new LinkedHashMap<>();
        request.headers().forEach((header) -> {
            headersMap.computeIfAbsent(header.getKey(), k -> new ArrayList<>()).add(header.getValue());
        });
        return headersMap;
    }

    /**
     * 获取 Cookies
     */
    public List<CookieEntity> getCookies(FullHttpRequest request) {
        List<CookieEntity> cookies = new ArrayList<>();
        String cookieHeader = request.headers().get(HttpHeaderNames.COOKIE);

        if (cookieHeader != null && !cookieHeader.isEmpty()) {
            Set<Cookie> decodedCookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
            for (Cookie cookie : decodedCookies) {
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

    /**
     * 将 FullHttpRequest 转换为 HttpRequestEntity
     */
    public HttpRequestEntity transHttpRequestEntity(FullHttpRequest request) {
        String requestURI = request.uri();
        HttpRequestEntity httpRequestEntity = new HttpRequestEntity();
        httpRequestEntity.url = requestURI;
        httpRequestEntity.contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        httpRequestEntity.methodType = request.method().name();
        httpRequestEntity.headers = getHeaders(request);
        httpRequestEntity.cookies = getCookies(request);

        // 解析 URL 参数
        QueryStringDecoder decoder = new QueryStringDecoder(httpRequestEntity.url);
        httpRequestEntity.path = decoder.path();
        Map<String, List<String>> uriAttributes = decoder.parameters();
        if (uriAttributes != null && !uriAttributes.isEmpty()) {
            uriAttributes.forEach((key, values) -> {
                httpRequestEntity.paramMap.put(key, values.getFirst());
            });
        }

        // 解析 Body 参数
        if (HttpMethod.POST.equals(request.method()) && httpRequestEntity.contentType != null) {
            httpRequestEntity.body = request.content().toString(CharsetUtil.UTF_8);
            if ("application/json".equals(httpRequestEntity.contentType)) {
                httpRequestEntity.sceneType = SceneType.JSON.name();
            } else if (httpRequestEntity.contentType.startsWith("multipart/form-data")) {
                httpRequestEntity.sceneType = SceneType.FORM.name();
                httpRequestEntity.paramMap.putAll(readBody(request));
            } else if (httpRequestEntity.contentType.contains("application/x-www-form-urlencoded")) {
                decoder = new QueryStringDecoder(httpRequestEntity.body, false);
                uriAttributes = decoder.parameters();
                if (uriAttributes != null && !uriAttributes.isEmpty()) {
                    uriAttributes.forEach((key, values) -> {
                        httpRequestEntity.paramMap.put(key, values.getFirst());
                    });
                }
            }
        }

        return httpRequestEntity;
    }

    /**
     * 解析 multipart/form-data 请求体
     */
    protected Map<String, Object> readBody(FullHttpRequest request) {
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
                                    ByteBuf byteBuf = fileUpload.getByteBuf();
                                    byte[] bytes = new byte[byteBuf.readableBytes()];
                                    byteBuf.readBytes(bytes);
                                    File file = new File(tempDir, fileUpload.getFilename());
                                    Files.write(Paths.get(tempDir, fileUpload.getFilename()), bytes);
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
                decoder.destroy();
            }
        } else {
            Logger.info("Not a multipart POST request.");
        }

        return paramMap;
    }

    /**
     * 使用 Reactor 响应式处理请求。
     * 直接订阅 Mono，使用回调处理响应，完全不阻塞 Netty I/O 线程。
     */
    protected void processRequestAsync(ChannelHandlerContext ctx, HttpRequestEntity httpRequestEntity) {
        controllerAdapter.dealHttpRequestReactive(httpRequestEntity)
                .subscribe(
                        // 成功回调
                        response -> {
                            if (ctx.channel().isActive()) {
                                sendHttpResponse(ctx, response);
                            } else {
                                Logger.warn("通道已关闭，无法发送响应，请求路径: {}", httpRequestEntity.path);
                            }
                        },
                        // 错误回调
                        error -> {
                            Logger.error(error, "异步请求处理异常，请求路径: {}", httpRequestEntity.path);
                            if (ctx.channel().isActive()) {
                                HttpResponseEntity response = handleError(error);
                                sendHttpResponse(ctx, response);
                            }
                        }
                );
    }

    /**
     * 处理异常，返回对应的 HttpResponseEntity
     */
    protected HttpResponseEntity handleError(Throwable error) {
        if (error instanceof TimeoutException) {
            return HttpResponseEntity.error("请求超时", 408);
        } else if (error instanceof BizException) {
            return HttpResponseEntity.error(error.getMessage(), 400);
        } else {
            return HttpResponseEntity.error("服务内部错误", 500);
        }
    }

    /**
     * 使用 HttpResponseHandlerFactory 选择合适的响应处理器并发送响应
     */
    protected void sendHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity) {
        HttpResponseHandler handler = HttpResponseHandlerFactory.getHandler(responseEntity);
        handler.dealHttpResponse(ctx, responseEntity);
    }
}