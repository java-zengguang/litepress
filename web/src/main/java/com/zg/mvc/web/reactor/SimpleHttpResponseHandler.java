package com.zg.mvc.web.reactor;

import com.zg.common.util.reflect.JsonUtil;
import com.zg.mvc.entity.HttpResponseEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
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

        if (responseEntity.result instanceof File file) {
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/force-download");
            response.headers().set("Content-Disposition", "attachment;filename=" + file.getName());
            // 设置 Content-Type 和 Content-Length
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
        } else if (responseEntity.result instanceof Serializable || responseEntity.result instanceof Collection<?> || responseEntity.result instanceof Map<?, ?>) {
            //转换json和文件
            responseEntity.result = JsonUtil.obj2String(responseEntity.result);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        } else {
            responseEntity.result = "";
        }

        return response;
    }

    @Override
    public void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity) {
        // 构建 HTTP 响应头
        FullHttpResponse response = transFullHttpResponse(responseEntity);

        // 如果结果是一个文件，则设置适当的头部信息并发送文件
        if (responseEntity.result instanceof File file) {

            ByteBuf buffer = Unpooled.buffer();
            try {
                if (file.exists()) {
                    // 使用 FileInputStream 读取文件
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] data = new byte[1024];
                        int bytesRead;
                        // 将文件内容写入 ByteBuf
                        while ((bytesRead = fis.read(data)) != -1) {
                            buffer.writeBytes(data, 0, bytesRead);
                        }
                    }
                } else {
                    Logger.info("文件找不到或者无法读取！" + file.getAbsoluteFile());
                }
            } catch (Exception e) {
                Logger.error(e, "文件读取失败");
            }
            response.content().writeBytes(buffer);
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    future.channel().close();
                    file.deleteOnExit();
                }
            });


        } else {
            ByteBuf buffer = Unpooled.copiedBuffer(responseEntity.result.toString().getBytes());
            response.content().writeBytes(buffer);
            // 对于非文件响应，直接发送整个响应
            ctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE); // 确保所有数据发送完毕后关闭连接
        }
        Logger.info("返回请求");
    }
}
