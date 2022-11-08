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
        Register register = new Register(providerConfig.registerURL);
        ProviderEntity providerEntity = register.findPriorityNode(providerName);

        ConsumerClient consumerClient = clientMap.get(providerEntity.path);
        if (consumerClient == null) {
            consumerClient = new ConsumerClient(providerEntity);
            clientMap.put(providerEntity.path, consumerClient);
        }
        return consumerClient;
    }

    private ConsumerClient(ProviderEntity providerEntity) throws IOException, KeeperException, InterruptedException {
        this.providerEntity=providerEntity;
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

    public ProviderEntity getProviderEntity() {
        return providerEntity;
    }
}
