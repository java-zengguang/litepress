package com.zg.direction.adapter;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.annotation.ProviderResovleAnnotation;
import com.zg.direction.entity.ProviderConfig;
import com.zg.common.init.Config;
import com.zg.direction.entity.ProviderEntity;
import com.zg.direction.register.ZookeeperUtil;
import com.zg.direction.server.ProviderService;
import com.zg.direction.server.ProviderServiceHandler;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

public class ProviderRegister {

    private static ProviderRegister providerRegister = null;

    private static Logger logger = LoggerFactory.getLogger(ProviderRegister.class);

    private static ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

    private static ZookeeperUtil zookeeperUtil;  //绑定zookeeper服务器

    private static Thread thread; //服务守护线程

    public static Table<String, String, ProviderEntity> providerTable = HashBasedTable.create();


    private ProviderRegister() throws IOException {
        zookeeperUtil = new ZookeeperUtil(providerConfig.registerURL);
    }


    public static synchronized ProviderRegister getInstance() throws IOException {
        if (providerRegister == null) {
            providerRegister = new ProviderRegister();
        }

        return providerRegister;

    }


    public void doServer() {
        //开启服务
        System.out.println("开始启动服务");
        ProviderService providerService = new ProviderService(new ProviderServiceHandler());
        if (thread == null) {
            thread = new Thread(providerService);
            thread.start();

        }

    }
    private Map<String, Object> loadProvider() {
        Map<String, Object> providerMap = null;
        ProviderResovleAnnotation pra = ProviderResovleAnnotation.getInstance();
        try {
            providerMap = pra.getProviders();
        } catch (ClassNotFoundException e) {
            logger.error("ProviderAdapter初始化错误", e);
        } catch (IllegalAccessException e) {
            logger.error("ProviderAdapter初始化错误", e);
        } catch (InstantiationException e) {
            logger.error("ProviderAdapter初始化错误", e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return providerMap;
    }

    private void doRegist(Map<String, Object> providerMap) throws IOException, InterruptedException, KeeperException, IllegalAccessException {
        Set<String> keySet = providerMap.keySet();
        for (String key : keySet) {
            String providerName = key;
            ProviderEntity providerEntity = (ProviderEntity) providerMap.get(key);
            String childPath = providerName + "/" + (new Date()).getTime();
            //  zookeeperUtil.createNode(path, value);
            zookeeperUtil.createChildNode(providerName, childPath, providerEntity);
            providerTable.put(providerName, childPath, providerEntity);
        }
        //  Thread.sleep(Integer.MAX_VALUE);
    }

    // 占用锁-锁定
    public  synchronized void occupy(String providerName,String path) throws InterruptedException, KeeperException {
       ProviderEntity providerEntity=  providerTable.get(providerName,path);
       providerEntity.occupy();
       zookeeperUtil.updateNode(path,JsonUtils.objectToJsonString(providerEntity));
    }


    // 占用锁-解锁
    public  synchronized void release(String providerName,String path,long times) throws InterruptedException, KeeperException {
        System.out.println(path+"调用时长"+times);
        ProviderEntity providerEntity=  providerTable.get(providerName,path);
        providerEntity.release(times);
        zookeeperUtil.updateNode(path,JsonUtils.objectToJsonString(providerEntity));
    }

    public ProviderEntity findPriorityNode(String providerName) throws KeeperException, InterruptedException {
        // return zookeeperUtil.findNodeOne(providerName);
        ProviderEntity result=null;
        Map<String,String> nodeMap=  zookeeperUtil.findChildNodeMap(providerName);
        Set<Map.Entry<String,String>> nodeSet= nodeMap.entrySet();
        if(nodeSet!=null&&nodeSet.size()>0) {
            for (Map.Entry<String, String> entry : nodeSet) {
                ProviderEntity providerEntity = (ProviderEntity) JsonUtils.jsonToObject(entry.getValue(), ProviderEntity.class);
                if(result==null){
                   result= providerEntity;
                }else if(result.priority>providerEntity.priority){
                    result=providerEntity;
                }else if(result.priority==providerEntity.priority&&result.times/(result.count+1)>providerEntity.times/(result.count+1)){
                    result=providerEntity; //当优先级相同，选择平均时长小的
                }
            }
        }
        return result;
    }


    public void doMain() throws IOException, InterruptedException, KeeperException, IllegalAccessException {
        doServer();//启动守护线程
        Map<String, Object> providerMap = loadProvider(); //扫描服务
        doRegist(providerMap);//注册服务
    }
}
