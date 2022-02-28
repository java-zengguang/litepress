package com.zg.network.im.client;

import com.alibaba.fastjson.JSON;
import com.zg.network.common.client.BaseClient;
import com.zg.network.common.client.BaseClientHandler;

public class IMClient extends BaseClient {
    public IMClient(BaseClientHandler<String> clientHandler, String host, int port) {
        super(clientHandler, host, port);
    }

    @Override
    public String resovleProtocol(Object object) {
        String json = JSON.toJSONString(object);
        return json;
    }
}
