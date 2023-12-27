package com.zg.direction.adapter;


import com.zg.common.bean.entity.OptionMGDB;
import com.zg.common.init.Config;
import com.zg.direction.entity.ProviderConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.tinylog.Logger;

import java.util.concurrent.CountDownLatch;

public class ConfigRegister {


    private static ProviderConfig providerConfig; //初始化配置
    private static CuratorFramework zkClient = null;

    //使用CountDownLatch等待zk创建完成，在执行主线程
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);


    public static void init() throws InterruptedException {
        providerConfig = (ProviderConfig) Config.getConfig("providerConfig");
        zkClient = CuratorFrameworkFactory.builder().connectString(providerConfig.registerURL)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .namespace("beanConfig")
                .build();
        zkClient.start();


        final TreeCache treeCache = new TreeCache(zkClient, "/");
        try {
            treeCache.start();
        } catch (Exception e) {
            Logger.error(e);
        }
        //添加错误监听器
        treeCache.getUnhandledErrorListenable().addListener((s, throwable) -> Logger.info(".错误原因：" + throwable.getMessage() + "\n==============\n"));

        //节点变化的监Logger.info听器
        treeCache.getListenable().addListener((curatorFramework, treeCacheEvent) -> {

            if (treeCacheEvent.getType() == TreeCacheEvent.Type.INITIALIZED) {
                countDownLatch.countDown();
                Logger.info("初始化！");
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.CONNECTION_RECONNECTED) {
                Logger.info("重新连接！");
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_ADDED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.info("创建！" + childData.getPath());
                if (childData.getData() != null && childData.getData().length > 0) {
                    String value = new String(childData.getData());
                    Config.updateBean2ConfigProperties(value);
                }
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.info("修改！" + childData.getPath());
                if (childData.getData() != null && childData.getData().length > 0) {
                    String value = new String(childData.getData());
                    Config.updateBean2ConfigProperties(value);
                }
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.info("删除！" + childData.getPath());

            }
        });
        countDownLatch.await();

    }

    //

    public static void main(String[] args) throws Exception {
        System.setProperty("projectRootPath", "D:\\work\\project\\databases\\direction\\target\\classes\\");
        init();
        OptionMGDB optionMGDB = (OptionMGDB) Config.getConfig("hello");
        System.out.println(optionMGDB.ip);

    }


}
