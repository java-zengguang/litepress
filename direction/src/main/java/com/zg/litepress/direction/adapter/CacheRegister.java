package com.zg.litepress.direction.adapter;


import com.zg.litepress.core.init.Config;
import com.zg.litepress.direction.entity.ProviderConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class CacheRegister {

    private static final Map<String, String> cacheMap = new HashMap<>();
    private static CacheRegister cacheRegister = null;  //单例
    private static ProviderConfig providerConfig; //初始化配置
    private static CuratorFramework zkClient = null;


    //使用CountDownLatch等待zk创建完成，在执行主线程
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    private CacheRegister() throws InterruptedException {

        init();
    }


    private static void init() throws InterruptedException {
        providerConfig = (ProviderConfig) Config.getConfig("providerConfig");
        zkClient = CuratorFrameworkFactory.builder().connectString(providerConfig.registerURL)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .namespace("cache")
                .build();
        zkClient.start();


        final TreeCache treeCache = new TreeCache(zkClient, "/");
        try {
            treeCache.start();
        } catch (Exception e) {
            Logger.error(e);
        }
        //添加错误监听器
        treeCache.getUnhandledErrorListenable().addListener((s, throwable) -> Logger.debug(".错误原因：" + throwable.getMessage() + "\n==============\n"));

        //节点变化的监Logger.debug听器
        treeCache.getListenable().addListener((curatorFramework, treeCacheEvent) -> {

            if (treeCacheEvent.getType() == TreeCacheEvent.Type.INITIALIZED) {
                countDownLatch.countDown();
                Logger.debug("初始化！");
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.CONNECTION_RECONNECTED) {
                Logger.debug("重新连接！");
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_ADDED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.debug("创建！" + childData.getPath());
                if (childData.getData() != null && childData.getData().length > 0) {
                    String value = new String(childData.getData());
                    cacheMap.put(childData.getPath(), value);
                }
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.debug("修改！" + childData.getPath());
                if (childData.getData() != null && childData.getData().length > 0) {
                    String value = new String(childData.getData());
                    cacheMap.put(childData.getPath(), value);
                }
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.debug("删除！" + childData.getPath());
                cacheMap.remove(childData.getPath());
            }
        });
        countDownLatch.await();

    }

    //这里开始提供API访问
    public static synchronized CacheRegister getInstance() throws Exception {
        if (cacheRegister == null) {
            cacheRegister = new CacheRegister();

        }
        return cacheRegister;

    }

    public static void main(String[] args) throws Exception {
        System.setProperty("projectRootPath", "D:\\work\\project\\databases\\direction\\target\\classes\\");
        CacheRegister cacheRegister = CacheRegister.getInstance();
        cacheRegister.put("/2", "老铁");
        cacheRegister.put("/2", "老王");

        Logger.debug(cacheRegister.get("/2"));
        Thread.sleep(1000);
        Logger.debug(cacheRegister.get("/2"));
        Thread.sleep(1000);
        Logger.debug(cacheRegister.get("/2"));

    }

    private void createNode(String path, String value) throws Exception {
        zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, value.getBytes());
        Logger.debug("success create znode: " + path);
    }

    public void put(String key, String value) throws Exception {
        if (zkClient.checkExists().forPath(key) == null) {
            createNode(key, value);
        } else {
            zkClient.setData().forPath(key, value.getBytes());
        }
    }

    public String get(String key) {
        return cacheMap.get(key);
    }


}
