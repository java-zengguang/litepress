package com.zg.direction.adapter;

import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.register.Register;
import com.zg.direction.server.ProviderService;
import com.zg.direction.server.ProviderServiceHandler;
import com.zg.init.Config;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Map;

public class ProviderAdapter {

    private ProviderConfig providerConfig= (ProviderConfig) Config.getConfig("providerConfig");

    public void init() throws InterruptedException, IllegalAccessException, KeeperException, IOException {


        //开启服务

        ProviderService providerService=new ProviderService(new ProviderServiceHandler(),providerConfig.DTPPort);
        Thread thread=new Thread(providerService);
        thread.start();

        //注册服务

        String registURL=providerConfig.registerURL;
        ProviderFactory providerFactory=ProviderFactory.getInstance();
        Map map=providerFactory.getProviderMap();
        Register register=new Register(registURL);
        register.registProvider(map);

    }


    public static void main(String args[]) throws InterruptedException, IOException, KeeperException, IllegalAccessException {
        ProviderAdapter providerAdapter=new ProviderAdapter();
        providerAdapter.init();
    }
}
