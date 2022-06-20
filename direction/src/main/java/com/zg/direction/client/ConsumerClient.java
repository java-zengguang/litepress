package com.zg.direction.client;

import com.zg.common.init.Config;
import com.zg.common.util.reflect.EntityUtils;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.entity.ProviderEntity;
import com.zg.direction.register.Register;
import com.zg.network.common.client.BaseClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.IOException;

public class ConsumerClient {


    public ConsumerClientHandler clientHandler;

    public BaseClient consumerClient;
    private ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

    private String providerName;

    private String host;

    private int port;

    private String className;

    public ConsumerClient( String providerName) throws InterruptedException, IOException, KeeperException {
        Register register = new Register(providerConfig.registerURL);
        String json = register.findNode(providerName);
        ProviderEntity providerEntity = (ProviderEntity) JsonUtils.jsonToObject(json, ProviderEntity.class);
        this.host = providerEntity.host;
        this.port = providerEntity.port;
        this.className = providerEntity.className;
        clientHandler = new ConsumerClientHandler();
        consumerClient = new BaseClient(clientHandler,host,port) {
            @Override
            public String resovleProtocol(Object object) throws IllegalAccessException {
                String json = null;
                json = EntityUtils.serialize(object);
                return json;
            }
        };


    }


    public String getClassName(){
        return this.className;
    }

    public void addRequest(Object request){
        consumerClient.addRequest(request);
    }

    public void doStart(){
       Thread thread=new Thread(consumerClient) ;
       thread.start();
    }


    public Object getResult(String id) {
      return   clientHandler.getResult(id);
    }
}
