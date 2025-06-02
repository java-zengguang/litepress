package com.zg.litepress.direction.adapter;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import com.zg.litepress.core.init.Config;
import com.zg.litepress.core.util.reflect.JsonUtil;
import com.zg.litepress.direction.annotation.ProviderResovleAnnotation;
import com.zg.litepress.direction.entity.ProviderConfig;
import com.zg.litepress.direction.entity.ProviderEntity;
import com.zg.litepress.direction.server.ProviderServiceHandler;
import com.zg.litepress.network.common.client.BaseKeepClient;
import com.zg.litepress.network.common.service.BaseKeepService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
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
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

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
                    ProviderEntity provider = (ProviderEntity) JsonUtil.string2Obj(new String(childData.getData()), ProviderEntity.class);
                    providerTable.put(provider.providerName, provider.path, provider);
                    Logger.debug("data:" + provider);
                }
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.debug("修改！" + childData.getPath());
                if (childData.getData() != null && childData.getData().length > 0) {
                    ProviderEntity provider = (ProviderEntity) JsonUtil.string2Obj(new String(childData.getData()), ProviderEntity.class);
                    providerTable.put(provider.providerName, provider.path, provider);
                }
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.debug("删除！" + childData.getPath());
                ProviderEntity provider = (ProviderEntity) JsonUtil.string2Obj(new String(childData.getData()), ProviderEntity.class);
                providerTable.remove(provider.providerName, provider.path);
                BaseKeepClient.close(provider.host, provider.port);

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

        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    BaseKeepService providerService = new BaseKeepService(new ProviderServiceHandler(),providerConfig.DTPPort);
                    providerService.doMain();
                }
            });
            thread.start();
        }
        Logger.debug("启动服务：" + thread.getId() + "：" + thread.getState());
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
            Logger.error(e);
        }
        return providerMap;
    }

    private void doRegist(Map<String, Object> providerMap) throws Exception {
        Set<String> keySet = providerMap.keySet();

        for (String key : keySet) {
            ProviderEntity providerEntity = (ProviderEntity) providerMap.get(key);
            providerEntity.clientVersion = "" + System.currentTimeMillis();
            String childPath = key + "/" + (new Date()).getTime();
            //  zookeeperUtil.createNode(path, value);
            createChildNode(key, childPath, providerEntity);
            Logger.debug("服务注册：" + childPath);

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
        Logger.debug("success create znode: " + path);
    }

    //创建节点需要同步操作
    private void createChildNode(String providerName, String childPath, ProviderEntity providerEntity) throws Exception {

        providerEntity.path = childPath;
        providerEntity.count = 0L;
        providerEntity.priority = 0;
        providerEntity.times = 0L;
        providerEntity.providerName = providerName;
        String value = JsonUtil.obj2String(providerEntity).toString();
        createNode(childPath, value);//创建子节点
        Logger.debug("success create znode: " + providerEntity.path);
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
        ProviderEntity provider = (ProviderEntity) JsonUtil.string2Obj(data, ProviderEntity.class);
        providerTable.put(provider.providerName, provider.path, provider);
        return provider;
    }


}
