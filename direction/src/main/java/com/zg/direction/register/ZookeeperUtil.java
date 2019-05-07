package com.zg.direction.register;


import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


public class ZookeeperUtil implements Watcher {


    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();


    public ZookeeperUtil() {
    }

    public ZookeeperUtil(String connectString) throws IOException {
        zk = new ZooKeeper(connectString, 5000,
                new ZookeeperUtil());
    }


    public void createNode(String path, String value) throws InterruptedException, KeeperException {
        connectedSemaphore.await();
        zk.create(path, value.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("success create znode: " + path);

    }

    public void updateNode(String path, String value, int version) throws InterruptedException, KeeperException {
        connectedSemaphore.await();
        zk.setData(path, value.getBytes(), version);
    }

    public void deleteNode(String path, int version) throws KeeperException, InterruptedException {
        connectedSemaphore.await();
        zk.delete(path, version);
    }

    public String findNode(String path) throws InterruptedException, KeeperException {
        connectedSemaphore.await();
        String data=new String(zk.getData(path,true,stat));
        return data;
    }

    public static void main(String[] args) throws Exception {
        String path = "/zk-book";

        String connectString = "127.0.0.1:2181";

        ZookeeperUtil zookeeperUtil=new ZookeeperUtil(connectString);
        //zookeeperUtil.createNode(path,"423");
        System.out.println(zookeeperUtil.findNode(path));

    }

    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            if (EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            } else if (event.getType() == EventType.NodeDataChanged) {
                try {
                    System.out.println("the data of znode " + event.getPath() + " is : " + new String(zk.getData(event.getPath(), true, stat)));
                    System.out.println("czxID: " + stat.getCzxid() + ", mzxID: " + stat.getMzxid() + ", version: " + stat.getVersion());
                } catch (Exception e) {
                }
            }
        }
    }
}

