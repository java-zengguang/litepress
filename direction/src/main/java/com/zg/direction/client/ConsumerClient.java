package com.zg.direction.client;

import com.zg.common.init.Config;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.entity.ProviderEntity;
import com.zg.direction.register.Register;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerClient {


    private static ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

    private ProviderEntity providerEntity;
    private SimpleClient simpleClient;

    private static Map<String, ConsumerClient> clientMap = new ConcurrentHashMap<>();


    public static ConsumerClient getInstance(String providerName) throws InterruptedException, IOException, KeeperException {
        ConsumerClient consumerClient = clientMap.get(providerName);
        if (consumerClient == null) {
            consumerClient = new ConsumerClient(providerName);
            clientMap.put(providerName, consumerClient);
        }
        return consumerClient;
    }

    private ConsumerClient(String providerName) throws IOException, KeeperException, InterruptedException {
        Register register = new Register(providerConfig.registerURL);
        String json = register.findNode(providerName);
        providerEntity = (ProviderEntity) JsonUtils.jsonToObject(json, ProviderEntity.class);
        simpleClient = SimpleClient.getInstance(providerEntity.host, providerEntity.port);

    }


    public String getClassName() {
        return this.providerEntity.getClassName();
    }

    public void addRequest(Object request) {
        simpleClient.addRequest(request);
    }


    public Object getResult(String id) {
        return simpleClient.getResult(id);
    }
}
