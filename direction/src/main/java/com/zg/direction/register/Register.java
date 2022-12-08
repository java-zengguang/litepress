package com.zg.direction.register;

import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.adapter.ProviderFactory;
import com.zg.direction.entity.ProviderEntity;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Register {

    private ZookeeperUtil zookeeperUtil;

    public Register() {
    }

    public Register(String connectString) throws IOException {
        zookeeperUtil = ZookeeperUtil.getInstance(connectString);
    }

    public static void main(String[] args) throws Exception {

        String connectString = "127.0.0.1:2181";

        ProviderFactory providerFactory = ProviderFactory.getInstance();
        Map map = providerFactory.getProviderMap();
        Register register = new Register(connectString);
        register.registProvider(map);


    }


    public void registProvider(Map<String, Object> map) throws InterruptedException, KeeperException, IllegalAccessException {

        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            String path = key;
            String value = JsonUtils.objectToJson(map.get(key)).toString();
          //  zookeeperUtil.createNode(path, value);
            zookeeperUtil.createChildNode(path,value);
        }
        Thread.sleep(Integer.MAX_VALUE);
    }


    public ProviderEntity findNode(String providerName) throws KeeperException, InterruptedException {
        String json= zookeeperUtil.findNodeOne(providerName);
        ProviderEntity providerEntity = (ProviderEntity) JsonUtils.jsonToObject(json, ProviderEntity.class);
        return providerEntity;
    }
    public ProviderEntity findPriorityNode(String providerName) throws KeeperException, InterruptedException {
        // return zookeeperUtil.findNodeOne(providerName);
        ProviderEntity result=null;
        Map<String,String> nodeMap=  zookeeperUtil.findChildNodeMap(providerName);
        Set<Map.Entry<String,String>> nodeSet= nodeMap.entrySet();
        for(Map.Entry<String,String> entry:nodeSet){
          ProviderEntity  providerEntity = (ProviderEntity) JsonUtils.jsonToObject(entry.getValue(), ProviderEntity.class);
          providerEntity.path=entry.getKey();
          if(result==null){
              result=providerEntity;
          }else if(providerEntity.getPriority()<result.getPriority()){
             result=providerEntity;
          }else if(providerEntity.getPriority()==result.getPriority()&&new Random().nextBoolean()){
              result=providerEntity;
          }
        }
        return result;
    }

    public synchronized void occupy(String path) throws InterruptedException, KeeperException {
        zookeeperUtil.occupy(path);
    };
    public synchronized void release(String path) throws InterruptedException, KeeperException {
        zookeeperUtil.release(path);
    };


}
