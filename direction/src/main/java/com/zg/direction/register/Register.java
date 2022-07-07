package com.zg.direction.register;

import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.adapter.ProviderFactory;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class Register {

    private ZookeeperUtil zookeeperUtil;

    public Register() {
    }

    public Register(String connectString) throws IOException {
        zookeeperUtil = new ZookeeperUtil(connectString);
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


    public String findNode(String providerName) throws KeeperException, InterruptedException {
        return zookeeperUtil.findNodeOne(providerName);
    }
}
