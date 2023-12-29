package com.zg.network.common.client;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.network.common.cache.BaseMessageCache;
import com.zg.network.entity.BaseTranslationProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.tinylog.Logger;


public class BaseKeepClientHandler extends ChannelInboundHandlerAdapter {

    private Class agreementClass;  //协议

    public BaseKeepClientHandler(Class agreementClass) {
        this.agreementClass = agreementClass;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        Logger.info("返回请求 message: " + msg);
        BaseTranslationProtocol baseTranslationProtocol = (BaseTranslationProtocol) EntityUtils.unSerialize((String) msg, agreementClass);
        BaseMessageCache.dealResponse(baseTranslationProtocol);
    }

}
