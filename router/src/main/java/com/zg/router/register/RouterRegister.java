package com.zg.router.register;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zg.common.init.Config;
import com.zg.common.init.Evn;
import com.zg.common.util.reflect.JsonUtil;
import com.zg.router.entity.RouterEntity;
import com.zg.router.entity.RouterRegisterConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.tinylog.Logger;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class RouterRegister<T> {

    private static RouterRegister cacheRegister = null;  //单例
    private static Table<String, String, Set<RouterEntity>> routeTable = HashBasedTable.create();  //服务类型 path 数据

    private RouterRegisterConfig routerRegisterConfig; //初始化配置
    private Map<String, CuratorFramework> curatorFrameworkMap = new HashMap<>();


    //使用CountDownLatch等待zk创建完成，在执行主线程

    private RouterRegister(RouterRegisterConfig routerRegisterConfig) throws InterruptedException {
        this.routerRegisterConfig = routerRegisterConfig;
    }


    private CuratorFramework getZkClient(String namespace) throws InterruptedException {
        if (curatorFrameworkMap.containsKey(namespace)) {
            return curatorFrameworkMap.get(namespace);
        }
        CuratorFramework zkClient = CuratorFrameworkFactory.builder().connectString(routerRegisterConfig.registerURL)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .namespace(namespace)
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
        CountDownLatch countDownLatch = new CountDownLatch(1);
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
                    RouterEntity router = JsonUtil.string2Obj(new String(childData.getData()), RouterEntity.class);
                    if (childData.getPath().equals(router.path + "/" + router.clientVersion)) {
                        Set<RouterEntity> routerEntities = routeTable.get(router.serviceType, router.path);
                        if (routerEntities == null) {
                            routerEntities = new HashSet<>();
                        }
                        routerEntities.add(router);
                        routeTable.put(namespace, router.path, routerEntities);
                        Logger.info(JsonUtil.obj2String(routeTable.row(namespace)));

                    }

                }
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED) {

                ChildData childData = treeCacheEvent.getData();
                Logger.info("修改！" + childData.getPath());
                if (childData.getData() != null && childData.getData().length > 0) {
                    RouterEntity router = JsonUtil.string2Obj(new String(childData.getData()), RouterEntity.class);
                    if (childData.getPath().equals(router.path + "/" + router.clientVersion)) {
                        Set<RouterEntity> routerEntities = routeTable.get(router.serviceType, router.path);
                        if (routerEntities == null) {
                            routerEntities = new HashSet<>();
                        }
                        routerEntities.add(router);
                        routeTable.put(namespace, router.path, routerEntities);
                        Logger.info(JsonUtil.obj2String(routeTable.row(namespace)));

                    }
                }
            }
            if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                ChildData childData = treeCacheEvent.getData();
                Logger.info("删除！" + childData.getPath());
                routeTable.remove(namespace, childData.getPath());

                if (childData.getData() != null && childData.getData().length > 0) {
                    RouterEntity router = JsonUtil.string2Obj(new String(childData.getData()), RouterEntity.class);
                    if (childData.getPath().equals(router.path + "/" + router.clientVersion)) {
                        routeTable.remove(namespace, router.path);
                        Logger.info(JsonUtil.obj2String(routeTable.row(namespace)));
                    }
                }
            }
        });
        countDownLatch.await();
        curatorFrameworkMap.put(namespace, zkClient);
        return zkClient;
    }

    //这里开始提供API访问
    public static synchronized RouterRegister getInstance(RouterRegisterConfig routerRegisterConfig) throws Exception {
        if (cacheRegister == null) {
            cacheRegister = new RouterRegister(routerRegisterConfig);
        }
        return cacheRegister;
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("projectRootPath", "D:\\work\\project\\databases\\direction\\target\\classes\\");
        RouterRegisterConfig routerRegisterConfig1 = new RouterRegisterConfig();
        routerRegisterConfig1.registerURL = "10.7.136.172:2181";
        Config.setConfig("routerRegisterConfig", routerRegisterConfig1);
        RouterRegister cacheRegister = RouterRegister.getInstance(routerRegisterConfig1);
        RouterEntity router = new RouterEntity();
        router.host = "12";
        router.port = 12;
        router.path = "/test/hello";
        router.serviceType = "event";
        router.clientVersion = "123";
        cacheRegister.putRouter(router);
        Thread.sleep(1000);
        RouterEntity router1 = new RouterEntity();
        router1.host = "12";
        router1.port = 12;
        router1.path = "/test/tt";
        router1.serviceType = "event";
        router1.clientVersion = "9089";
        cacheRegister.putRouter(router1);
        Thread.sleep(1000);

        Logger.info("写入");
    }


    //添加版本号
    public void putRouter(RouterEntity router) throws Exception {
        CuratorFramework zkClient = getZkClient(router.serviceType);
        if (zkClient.checkExists().forPath(router.path + "/" + router.clientVersion) == null) {
            zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(router.path + "/" + router.clientVersion, JsonUtil.obj2String(router).getBytes());
        }
    }

    public RouterEntity getRouter(String serviceType, String path) {
        Set routers = routeTable.get(serviceType, path);
        if (routers == null || routers.size() == 0) {
            return null;
        }
        return routeTable.get(serviceType, path).stream().findAny().get();
    }


}
