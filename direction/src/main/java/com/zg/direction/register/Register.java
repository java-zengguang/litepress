package com.zg.direction.register;

import com.zg.direction.adapter.ProviderFactory;
import com.zg.util.reflect.JsonUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Register implements Watcher {


    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();


    public Register() {
    }

    public Register(String connectString) throws IOException {
        zk = new ZooKeeper(connectString, 5000,
                new Register());
    }

    public static void main(String[] args) throws Exception {

        String connectString = "127.0.0.1:2181";

        ProviderFactory providerFactory = ProviderFactory.getInstance();
        Map map = providerFactory.getProviderMap();
        Register register = new Register(connectString);
        register.registProvider(map);


    }

    public void registProvider(Map<String, Object> map) throws InterruptedException, KeeperException, IllegalAccessException {
        connectedSemaphore.await();

        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            String path = key;
            String value = JsonUtils.objectToJson(map.get(key)).toString();
            zk.create(path, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path);
            System.out.println("success create data: " + value);
        }
        Thread.sleep(Integer.MAX_VALUE);

    }

    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            if (Event.EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            } else if (event.getType() == Event.EventType.NodeDataChanged) {
                try {
                    System.out.println("the data of znode " + event.getPath() + " is : " + new String(zk.getData(event.getPath(), true, stat)));
                    System.out.println("czxID: " + stat.getCzxid() + ", mzxID: " + stat.getMzxid() + ", version: " + stat.getVersion());
                } catch (Exception e) {
                }
            }
        }
    }
}
