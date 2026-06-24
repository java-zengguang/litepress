package io.github.java_zengguang.litepress.network.common.client;

import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.network.common.cache.BaseMessageCache;
import io.github.java_zengguang.litepress.network.entity.BaseTranslationProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.tinylog.Logger;


public abstract class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private Class agreementClass;  //协议

    public NettyClientHandler(Class agreementClass) {
        this.agreementClass = agreementClass;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        // 异常处理逻辑
        Logger.error(e);
        // 关闭Channel
        ctx.close();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        Logger.debug("返回请求 message: " + msg);
        BaseTranslationProtocol baseTranslationProtocol = (BaseTranslationProtocol) JsonUtil.string2Obj((String) msg, agreementClass);
        this.deal(baseTranslationProtocol);
    }


    public abstract void deal(BaseTranslationProtocol translationProtocol);

}
