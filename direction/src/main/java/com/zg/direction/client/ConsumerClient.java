package com.zg.direction.client;

import com.zg.direction.adapter.ProviderRegister;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import com.zg.direction.entity.ProviderEntity;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

public class ConsumerClient {
    private ProviderEntity providerEntity;
    private SimpleClient simpleClient;

    public final static Map<String,Thread> synRequestThreadMap=new Hashtable<>();  //消息发送阻塞队列，发送时将现线程放入，返回时唤醒线程




    public static ConsumerClient getInstance(String providerName) throws InterruptedException, IOException, KeeperException {
        ProviderRegister providerRegister = ProviderRegister.getInstance();
        ProviderEntity providerEntity = providerRegister.findPriorityNode(providerName);
        ConsumerClient consumerClient = new ConsumerClient(providerEntity);
        return consumerClient;
    }

    private ConsumerClient(ProviderEntity providerEntity) throws IOException, KeeperException, InterruptedException {
        this.providerEntity = providerEntity;
        simpleClient = SimpleClient.getInstance(providerEntity.host, providerEntity.port,providerEntity.clientVersion);

    }


    public String getClassName() {
        return this.providerEntity.getClassName();
    }


    public DTPResponse addASynRequest(DTPRequest request) throws Exception {
        simpleClient.addRequest(request);
        DTPResponse response = new DTPResponse();
        response.id = request.id;
        response.success = true;
        return response;

    }

    //同步返回结果
    public synchronized DTPResponse addSynRequest(DTPRequest request) throws Exception {
        synRequestThreadMap.put(request.id,Thread.currentThread());
        simpleClient.addRequest(request);
        LockSupport.parkUntil(System.currentTimeMillis()+10*1000);
        DTPResponse response = (DTPResponse) getResult(request.id);

        return response;

    }


    public Object getResult(String id) {
        return simpleClient.getResult(id);
    }

    public ProviderEntity getProviderEntity() {
        return providerEntity;
    }
}
