package com.zg.direction.client;

import com.zg.direction.entity.DTPResponse;
import com.zg.network.common.MessgeReceivedListener;
import com.zg.network.common.client.BaseClientHandler;
import com.zg.util.reflect.EntityUtils;
import io.netty.channel.ChannelHandlerContext;

public class ConsumerClientHandler extends BaseClientHandler<String> {

    private boolean received = false;

    private Object result;

    public static void main(String args[]) throws InstantiationException, IllegalAccessException {

        ConsumerClientHandler consumerClientHandler = new ConsumerClientHandler();
        String json = "{\"success\":true,\"error\":null,\"resultData\":{\"x\":1},\"resultType\":\"com.zg.direction.TestEntity\"}";
        consumerClientHandler.unSerialize(json, DTPResponse.class);
    }

    private Object unSerialize(String str, Class classType) throws IllegalAccessException, InstantiationException {
        Object object = EntityUtils.unSerialize(str, classType);
        return object;
    }

    private String serialize(Object object) throws IllegalAccessException {
        String data = EntityUtils.serialize(object);
        return data;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        LOGGER.info("msg >>" + msg);
        DTPResponse response = (DTPResponse) unSerialize(msg, DTPResponse.class);
        if (response.success) {
            LOGGER.info("操作成功");
            if (!"".equals(response.resultType) && !"NULL".equals(response.resultType)) {
                Class classType = Class.forName(response.resultType);
                result = unSerialize(response.resultData.toString(), classType);
                received = true;
            }
        }
    }

    public synchronized Object getResult() throws InterruptedException {
        if (received) {
            received = false;
            return result;
        } else {
            Thread.sleep(1000);
            return getResult();
        }
    }

    @Override
    public void remove(MessgeReceivedListener messgeReceivedListener) {

    }

    @Override
    public void addMessgeReceivedListener(MessgeReceivedListener messgeReceivedListener) {

    }
}
