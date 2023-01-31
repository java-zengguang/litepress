package com.zg.direction.client;

import com.zg.direction.adapter.ProviderRegister;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import com.zg.direction.entity.ProviderEntity;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class ConsumerClient {
    private ProviderEntity providerEntity;
    private SimpleClient simpleClient;

    /*    private static Map<String, ConsumerClient> clientMap = new ConcurrentHashMap<>();*/


    public static ConsumerClient getInstance(String providerName) throws InterruptedException, IOException, KeeperException {
        ProviderRegister providerRegister = ProviderRegister.getInstance();
        ProviderEntity providerEntity = providerRegister.findPriorityNode(providerName);
        ConsumerClient consumerClient = new ConsumerClient(providerEntity);
        return consumerClient;
    }

    private ConsumerClient(ProviderEntity providerEntity) throws IOException, KeeperException, InterruptedException {
        this.providerEntity = providerEntity;
        simpleClient = SimpleClient.getInstance(providerEntity.host, providerEntity.port);

    }


    public String getClassName() {
        return this.providerEntity.getClassName();
    }


    public DTPResponse addASynRequest(DTPRequest request) throws Exception {
        simpleClient.addRequest(request);
        DTPResponse response = new DTPResponse();
        response.id=request.id;
        response.success=true;
        return response;

    }

    //同步返回结果
    public DTPResponse addSynRequest(DTPRequest request) throws Exception {
        simpleClient.addRequest(request);
        DTPResponse response = null;
        int maxWait = 10 * 60 * 1000;
        int oneWait = 50;
        int currentWait = 0;
        do {
            Thread.sleep(oneWait);
            response = (DTPResponse) getResult(request.id);
            currentWait = currentWait + oneWait;
            if (currentWait > maxWait) {
                throw new Exception("请求超时");
            }
        } while (response == null);
        if (!response.success) {
            throw new Exception(response.error);
        }

        return response;

    }


    public Object getResult(String id) {
        return simpleClient.getResult(id);
    }

    public ProviderEntity getProviderEntity() {
        return providerEntity;
    }
}
