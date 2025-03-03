package com.zg.mvc.web.reactor;

import com.zg.common.util.reflect.JsonUtil;
import com.zg.mvc.entity.HttpResponseEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import org.tinylog.Logger;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class SimpleHttpResponseHandler extends BaseHttpResponseHandler {
    private FullHttpResponse transFullHttpResponse(HttpResponseEntity responseEntity) {
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

    @Override
    public void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity) {
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
}
