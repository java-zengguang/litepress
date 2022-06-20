package com.zg.direction.client;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.direction.entity.DTPResponse;
import com.zg.network.common.MessgeReceivedListener;
import com.zg.network.common.client.BaseClientHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class ConsumerClientHandler<T> extends BaseClientHandler<String> {

    private Map<String, Object> resultMap = new HashMap();

    public static void main(String args[]) throws InstantiationException, IllegalAccessException {

        ConsumerClientHandler consumerClientHandler = new ConsumerClientHandler();
        String json = "{\"success\":true,\"error\":null,\"resultData\":{\"x\":1},\"resultType\":\"com.zg.direction.TestEntity\"}";
        consumerClientHandler.unSerialize(json, DTPResponse.class);
    }

    private Object unSerialize(String str, Class classType) {
        Object object = EntityUtils.unSerialize(str, classType);
        return object;
    }

    private String serialize(Object object) {
        String data = EntityUtils.serialize(object);
        return data;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("msg >>" + msg);
        DTPResponse response = (DTPResponse) unSerialize(msg, DTPResponse.class);
        if (response != null) {
            resultMap.put(response.id, response);
        }


    }

    public  Object getResult(String id)   {
        DTPResponse response = (DTPResponse) resultMap.get(id);
        return response;
    }

    @Override
    public void remove(MessgeReceivedListener messgeReceivedListener) {

    }

    @Override
    public void addMessgeReceivedListener(MessgeReceivedListener messgeReceivedListener) {

    }
}
