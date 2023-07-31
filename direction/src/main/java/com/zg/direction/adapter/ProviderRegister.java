package com.zg.direction.adapter;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zg.common.init.Config;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.annotation.ProviderResovleAnnotation;
import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.entity.ProviderEntity;
import com.zg.direction.server.ProviderService;
import com.zg.direction.server.ProviderServiceHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.tinylog.Logger;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class ProviderRegister {

    public static final Table<String, String, ProviderEntity> providerTable = HashBasedTable.create();
    private static ProviderRegister providerRegister = null;  //单例
    private static ProviderConfig providerConfig; //初始化配置
    private static Thread thread; //服务守护线程
    private static CuratorFramework zkClient = null;


    //使用CountDownLatch等待zk创建完成，在执行主线程
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private ProviderRegister() throws InterruptedException {
        init();
    }


    private static void init() throws InterruptedException {
        providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

        zkClient = CuratorFrameworkFactory.builder().connectString(providerConfig.registerURL)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .namespace("provider")
                .build();
        zkClient.start();


        final TreeCache treeCache = new TreeCache(zkClient, "/");
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //添加错误监听器
        treeCache.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            public void unhandledError(String s, Throwable throwable) {
                Logger.info(".错误原因：" + throwable.getMessage() + "\n==============\n");
            }
        });

        //节点变化的监Logger.info听器
        treeCache.getListenable().addListener(new TreeCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {

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
                        ProviderEntity provider = (ProviderEntity) JsonUtils.jsonToObject(new String(childData.getData()), ProviderEntity.class);
                        providerTable.put(provider.providerName, provider.path, provider);
                        Logger.info("data:" + provider);
                    }
                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
                    ChildData childData = treeCacheEvent.getData();
                    Logger.info("修改！" + childData.getPath());
                    if (childData.getData() != null && childData.getData().length > 0) {
                        ProviderEntity provider = (ProviderEntity) JsonUtils.jsonToObject(new String(childData.getData()), ProviderEntity.class);
                        providerTable.put(provider.providerName, provider.path, provider);
                    }
                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                    ChildData childData = treeCacheEvent.getData();
                    Logger.info("删除！" + childData.getPath());
                    ProviderEntity provider = (ProviderEntity) JsonUtils.jsonToObject(new String(childData.getData()), ProviderEntity.class);
                    providerTable.remove(provider.providerName, provider.path);
                }
            }
        });


        countDownLatch.await();
    }

    public static synchronized ProviderRegister getInstance() throws Exception {
        if (providerRegister == null) {
            providerRegister = new ProviderRegister();

        }
        return providerRegister;

    }


    public void doServer() {
        //开启服务
        ProviderService providerService = new ProviderService(new ProviderServiceHandler());
        if (thread == null) {
            thread = new Thread(providerService);
            thread.start();
        }
        Logger.info("启动服务：" + thread.getId() + "：" + thread.getState());
    }

    private Map<String, Object> loadProvider() {
        Map<String, Object> providerMap = null;
        ProviderResovleAnnotation pra = ProviderResovleAnnotation.getInstance();
        try {
            providerMap = pra.getProviders();
        } catch (ClassNotFoundException e) {
            Logger.error("ProviderAdapter初始化错误", e);
        } catch (IllegalAccessException e) {
            Logger.error("ProviderAdapter初始化错误", e);
        } catch (InstantiationException e) {
            Logger.error("ProviderAdapter初始化错误", e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return providerMap;
    }

    private void doRegist(Map<String, Object> providerMap) throws Exception {
        Set<String> keySet = providerMap.keySet();

        for (String key : keySet) {
            String providerName = key;
            ProviderEntity providerEntity = (ProviderEntity) providerMap.get(key);
            providerEntity.clientVersion = "" + System.currentTimeMillis();
            String childPath = providerName + "/" + (new Date()).getTime();
            //  zookeeperUtil.createNode(path, value);
            createChildNode(providerName, childPath, providerEntity);
            Logger.info("服务注册：" + childPath);

        }
        //  Thread.sleep(Integer.MAX_VALUE);
    }


    //随机
    public ProviderEntity findPriorityNode(String providerName) throws Exception {
        // return zookeeperUtil.findNodeOne(providerName);
        ProviderEntity result = null;
        Map<String, ProviderEntity> nodeMap = providerTable.row(providerName);
        if (nodeMap == null || nodeMap.isEmpty()) {
            getProviderByName(providerName);
        }
        Set<Map.Entry<String, ProviderEntity>> nodeSet = nodeMap.entrySet();
        if (nodeSet != null && nodeSet.size() > 0) {
            List<Map.Entry<String, ProviderEntity>> nodeList = new ArrayList<>(nodeSet);
            Random random = new Random();
            Integer index = random.nextInt(nodeSet.size());
            Map.Entry<String, ProviderEntity> entry = nodeList.get(index);
            result = entry.getValue();
        }

        return result;
    }

    public void doMain() throws Exception {
        doServer();//启动守护线程
        Map<String, Object> providerMap = loadProvider(); //扫描服务
        doRegist(providerMap);//注册服务
    }


    private void createNode(String path, String value) throws Exception {
        zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, value.getBytes());
        Logger.info("success create znode: " + path);
    }

    //创建节点需要同步操作
    private void createChildNode(String providerName, String childPath, ProviderEntity providerEntity) throws Exception {

        providerEntity.path = childPath;
        providerEntity.count = 0L;
        providerEntity.priority = 0;
        providerEntity.times = 0L;
        providerEntity.providerName = providerName;
        String value = JsonUtils.objectToJson(providerEntity).toString();
        createNode(childPath, value);//创建子节点
        Logger.info("success create znode: " + providerEntity.path);
    }


    private void getProviderByName(String providerName) throws Exception {
        //去远程获取
        List<String> dataList = zkClient.getChildren().forPath(providerName);
        for (String data : dataList) {
            ProviderEntity provider = findProviderByPath(providerName + "/" + data);
            providerTable.put(provider.providerName, provider.path, provider);
        }

    }

    private ProviderEntity findProviderByPath(String path) throws Exception {
        //去远程获取
        String data = new String(zkClient.getData().forPath(path));
        ProviderEntity provider = (ProviderEntity) JsonUtils.jsonToObject(data, ProviderEntity.class);
        providerTable.put(provider.providerName, provider.path, provider);
        return provider;
    }


}
