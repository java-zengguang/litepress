package com.zg.mvc.web.reactor;

import com.zg.common.util.reflect.JsonUtil;
import com.zg.mvc.entity.HttpResponseEntity;
import com.zg.mvc.web.sse.SSEDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.tinylog.Logger;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class StreamHttpResponseHandler extends BaseHttpResponseHandler {
    @Override
    public void dealHttpResponse(ChannelHandlerContext ctx, HttpResponseEntity responseEntity) throws InterruptedException {
        //转换json和文件

        if (responseEntity.result instanceof BlockingQueue blockingQueue) {
            Thread vt = Thread.ofVirtual().start(() -> {
                Logger.info("SSE虚拟线程循环写入开始！");
                // 从阻塞队列获取数据
                String flag = "";
                do {
                    try {
                        Object object = blockingQueue.take();
                        if (object instanceof SSEDto data) {
                            Logger.info("获取数据 " + data.toSseFormat());
                            flag = data.flag;
                            // 响应设置为 SSE 格式
                            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK
                                    , Unpooled.copiedBuffer(data.toSseFormat().getBytes()));
                            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
                            response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
                            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                            ctx.writeAndFlush(response).addListener((ChannelFutureListener) future -> {
                                if (future.isSuccess()) {
                                    Logger.info("写入数据成功");
                                } else {
                                    Logger.info("写入数据失败");
                                }
                                if ("stop".equals(data.flag)) {
                                    Logger.info("关闭通道");
                                    future.channel().close();
                                }
                            });


                        } else {
                            Logger.info("不支持的写入格式");
                            break;
                        }

                    } catch (Exception e) {
                        Logger.error(e);
                        break;
                    }
                } while ("keep".equals(flag));
                Logger.info("写入线程循环关闭！");
            });
        }


    }
}
