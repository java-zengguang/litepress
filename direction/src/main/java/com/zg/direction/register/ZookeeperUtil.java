package com.zg.direction.register;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public JSONObject findNodeJson(String path) throws InterruptedException, KeeperException {
        JSONObject jsonObject= JSONObject.parseObject(findNode(path));
        if(jsonObject!=null){
        jsonObject.put("name",path);
        }
        return jsonObject;
    }

    public List<Map<String,String>> findChildNodes(String path) throws InterruptedException, KeeperException {
        connectedSemaphore.await();
        List<Map<String,String>> resultList=new ArrayList<>();
        List<String> list= zk.getChildren(path,true,stat);
        for(String key:list){
            String data=new String(zk.getData("/"+key,true,stat));
            Map map=new HashMap();
            map.put(key,data);
            resultList.add(map);
        }
        return resultList;
    }

    public JSONArray findChildNodesJson(String path) throws InterruptedException, KeeperException {
        connectedSemaphore.await();
        JSONArray jsonArray=new JSONArray();
        List<String> list= zk.getChildren(path,true,stat);
        for(String key:list){
            JSONObject jsonObject=findNodeJson("/"+key);
            if(jsonObject!=null) {
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
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

