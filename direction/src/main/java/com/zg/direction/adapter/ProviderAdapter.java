package com.zg.direction.adapter;

import com.zg.common.init.Config;
import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.register.Register;
import com.zg.direction.register.ZookeeperBoot;
import com.zg.direction.server.ProviderService;
import com.zg.direction.server.ProviderServiceHandler;
import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.IOException;
import java.util.Map;

public class ProviderAdapter {

    private ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

    public static void main(String args[]) throws InterruptedException, IOException, KeeperException, IllegalAccessException, QuorumPeerConfig.ConfigException {
        ProviderAdapter providerAdapter = new ProviderAdapter();
        providerAdapter.init();
    }

    public synchronized void init() throws InterruptedException, IllegalAccessException, KeeperException, IOException, QuorumPeerConfig.ConfigException {

        //启动本地zookeperboot服务
/*        ZookeeperBoot zookeeperBoot = new ZookeeperBoot(this);
        Thread thread1=new Thread(zookeeperBoot);
        thread1.start();
        System.out.println("等待zookeeper启动");
        this.wait(10000);*/

        //开启服务
        System.out.println("开始启动服务");
        ProviderService providerService = new ProviderService(new ProviderServiceHandler(), providerConfig.DTPPort);
        Thread thread = new Thread(providerService);
        thread.start();

        //注册服务
        System.out.println("开始注册服务");
        String registURL = providerConfig.registerURL;
        ProviderFactory providerFactory = ProviderFactory.getInstance();
        Map map = providerFactory.getProviderMap();
        Register register = new Register(registURL);
        register.registProvider(map);

    }
}
