package com.zg.mvc.web.reactor;

import com.zg.common.util.reflect.JsonUtil;
import com.zg.mvc.entity.CookieEntity;
import com.zg.mvc.entity.HttpRequestEntity;
import com.zg.mvc.entity.HttpResponseEntity;
import com.zg.mvc.enums.SceneType;
import com.zg.mvc.web.adapter.HttpNettyControllerAdapter;
import com.zg.mvc.web.sse.SSE2NettyHandler;
import com.zg.mvc.web.sse.SSEHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.util.CharsetUtil;
import org.tinylog.Logger;

import java.io.*;
import java.util.*;

// 自定义请求处理器
public class SimpleHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    public static final List<String> ssePaths = List.of("/sse");
    private final HttpNettyControllerAdapter controllerAdapter = HttpNettyControllerAdapter.getInstance();


    public FullHttpResponse transFullHttpResponse(HttpResponseEntity responseEntity)   {
        // 创建 HTTP 响应
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(responseEntity.statusCode));
        if (responseEntity.headers != null && !responseEntity.headers.isEmpty()) {
            responseEntity.headers.forEach((key, values) -> {
                if (values != null && !values.isEmpty()) {
                    values.forEach(value -> response.headers().add(key, value));
                }
            });
        }
        if (responseEntity.cookies != null && !responseEntity.cookies.isEmpty()) {
            responseEntity.cookies.forEach(cookieEntity -> response.headers().add(HttpHeaderNames.SET_COOKIE, cookieEntity.toCookieString().trim()));

        }


        //转换json和文件
        if (responseEntity.result instanceof Serializable || responseEntity.result instanceof Collection<?> || responseEntity.result instanceof Map<?, ?>) {
            responseEntity.result = JsonUtil.obj2String(responseEntity.result);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        }
        if (responseEntity.result instanceof String) {
            ByteBuf buffer = Unpooled.copiedBuffer(responseEntity.result.toString().getBytes());
            response.content().writeBytes(buffer);
        }
        if (responseEntity.result instanceof File file) {
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/force-download");
            response.headers().set("Content-Disposition", "attachment;filename=" + file.getName());
            // 设置 Content-Type 和 Content-Length
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
        }


        return response;
    }


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

    public HttpRequestEntity transHttpRequestEntity(FullHttpRequest request)  {
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
            } else if (httpRequestEntity.contentType.equals("multipart/form-data")) {
                httpRequestEntity.sceneType = SceneType.FORM.name();
                httpRequestEntity.paramMap.putAll(readBody(request));
            }
        }
        return httpRequestEntity;
    }

    private Map<String, Object> readBody(FullHttpRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        List<File> files = new ArrayList<>();
        if ("multipart/form-data".equalsIgnoreCase(request.headers().get("Content-Type"))) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            try {
                while (decoder.hasNext()) {
                    InterfaceHttpData data = decoder.next();
                    if (data != null) {
                        switch (data.getHttpDataType()) {
                            case Attribute:
                                MemoryAttribute attribute = (MemoryAttribute) data;
                                paramMap.put(attribute.getName(), attribute.getValue());
                                break;
                            case FileUpload:
                                FileUpload fileUpload = (FileUpload) data;
                                if (fileUpload.isCompleted() && !fileUpload.getFilename().isEmpty()) {
                                    files.add(fileUpload.getFile());
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


    private void dealSSE(ChannelHandlerContext ctx, HttpRequestEntity httpRequestEntity)   {
        String groupId = (String) httpRequestEntity.paramMap.get("groupId");
        String clientId = (String) httpRequestEntity.paramMap.get("clientId");
        SSEHandler sseHandler = SSE2NettyHandler.getInstance();
        sseHandler.createSSE(ctx, groupId);
        Logger.info("创建SSE链接  groupId: " + groupId + " clientId:" + clientId);
    }

    private void dealController(ChannelHandlerContext ctx, HttpRequestEntity httpRequestEntity) throws Exception {
        // 处理接收到的 HTTP 请求
        HttpResponseEntity responseEntity = controllerAdapter.dealHttpRequest(httpRequestEntity);

        // 构建 HTTP 响应头
        FullHttpResponse response = transFullHttpResponse(responseEntity);

        // 如果结果是一个文件，则设置适当的头部信息并发送文件
        if (responseEntity.result instanceof File file) {
            // 创建 DefaultFileRegion 对象来流式传输文件
            ctx.write(response); // 先写入响应头
            ctx.write(new DefaultFileRegion(file, 0, file.length())); // 再写入文件内容
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
                    .addListener(ChannelFutureListener.CLOSE); // 确保所有数据发送完毕后关闭连接
        } else {
            // 对于非文件响应，直接发送整个响应
            ctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE); // 确保所有数据发送完毕后关闭连接
        }
        Logger.info("返回请求");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 处理接收到的 HTTP 请求
        HttpRequestEntity httpRequestEntity = transHttpRequestEntity(msg);
        if (ssePaths.contains(httpRequestEntity.path)) {
            dealSSE(ctx, httpRequestEntity); //创建SSE监听
        } else {
            dealController(ctx, httpRequestEntity);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 发生异常时关闭连接
        Logger.error(cause, "netty网络异常！");
        ctx.close();
    }
}