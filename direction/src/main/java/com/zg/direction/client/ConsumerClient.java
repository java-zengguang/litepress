package com.zg.direction.client;

import com.zg.network.common.client.BaseClient;
import com.zg.network.common.client.BaseClientHandler;
import com.zg.util.reflect.EntityUtils;

public class ConsumerClient extends BaseClient {
    public ConsumerClient(BaseClientHandler<String> clientHandler, String host, int port) {
        super(clientHandler, host, port);
    }

    @Override
    public String resovleProtocol(Object object) {
        String json = null;
        json = EntityUtils.serialize(object);
        return json;
    }
}
