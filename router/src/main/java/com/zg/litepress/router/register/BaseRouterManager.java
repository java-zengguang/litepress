package com.zg.litepress.router.register;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import com.zg.litepress.core.error.BizException;
import com.zg.litepress.core.util.reflect.JsonUtil;
import com.zg.litepress.router.entity.RouterEntity;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class BaseRouterManager implements ServiceManager {
    private final static Map<RouterEntity,RouterService> routerServiceMap=new HashMap<>();
    private Table<String, String, RouterEntity> routeTable = HashBasedTable.create();  //name version  RouterEntity
    private String registerURL;
    private String namespace;
    private String routerType;
    private CuratorFramework curatorFramework;

    public BaseRouterManager(String registerURL, String namespace, String routerType) throws Exception {
        this.registerURL = registerURL;
        this.namespace = namespace;
        this.routerType = routerType;
        this.curatorFramework = getZkClient(routerType);

    }




    private CuratorFramework getZkClient(String routerType) throws Exception {
        CuratorFramework zkClient = CuratorFrameworkFactory.builder().connectString(registerURL)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .namespace(namespace)
                .build();
        zkClient.start();
        final TreeCache treeCache = new TreeCache(zkClient, "/" + routerType);
        treeCache.start();
        //添加错误监听器
        treeCache.getUnhandledErrorListenable().addListener((s, throwable) -> Logger.info(".错误原因：" + throwable.getMessage() + "\n==============\n"));
        //使用CountDownLatch等待zk创建完成，在执行主线程
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //节点变化的监Logger.info听器
        treeCache.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
            //异步线程池
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.INITIALIZED) {
                    countDownLatch.countDown();
                    Logger.info("初始化！");
                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.CONNECTION_RECONNECTED) {
                    Logger.info("重新连接！");
                    routerServiceMap.forEach((router,routerService)->{
                        try {
                            this.restart(router);
                            Logger.info(router.name+"重新启动成功！");
                        } catch (Exception e) {
                            Logger.info(router.name+"重新启动失败！");
                        }
                    });

                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_ADDED) {
                    ChildData childData = treeCacheEvent.getData();
                    Logger.info("创建！" + childData.getPath());
                    if (childData.getData() != null && childData.getData().length > 0) {
                        RouterEntity router = JsonUtil.string2Obj(new String(childData.getData()), RouterEntity.class);
                        if (childData.getPath().equals("/" + router.routerType + router.path + "/" + router.version)) {
                            routeTable.put(router.name, router.version, router);
                        }

                    }
                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
                    ChildData childData = treeCacheEvent.getData();
                    Logger.info("修改！" + childData.getPath());
                    if (childData.getData() != null && childData.getData().length > 0) {
                        RouterEntity router = JsonUtil.string2Obj(new String(childData.getData()), RouterEntity.class);
                        if (childData.getPath().equals("/" + router.routerType + router.path + "/" + router.version)) {
                            routeTable.put(router.name, router.version, router);
                        }
                    }
                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                    ChildData childData = treeCacheEvent.getData();
                    Logger.info("删除！" + childData.getPath());

                    if (childData.getData() != null && childData.getData().length > 0) {
                        RouterEntity router = JsonUtil.string2Obj(new String(childData.getData()), RouterEntity.class);
                        if (childData.getPath().equals("/" + router.routerType + router.path + "/" + router.version)) {
                            routeTable.remove(router.name, router.version);
                        }
                        if (childData.getPath().equals("/" + router.routerType + router.path)) {
                            routeTable.row(router.name).clear();
                        }

                    }
                }

            });
        });


        //暂时渠道Zookeep重新启动客户端，会导致数据丢失
/*        zkClient.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.LOST) {
                Logger.error("Zookeeper 连接丢失，尝试重新连接...");
                // 尝试重新连接
                zkClient.start();
            } else if (newState == ConnectionState.RECONNECTED) {
                Logger.info("Zookeeper 重新连接成功！");
            }
        });*/
        // 设置超时时间为 10 秒
        boolean initialized = countDownLatch.await(30, TimeUnit.SECONDS);
        if (!initialized) {
            Logger.error("Zookeeper 初始化超时！");
            throw new BizException("Zookeeper 初始化超时");
        }
        return zkClient;
    }


    private void putRouter(RouterEntity router) throws Exception {
        if (!routerType.equals(router.routerType)) {
            throw new BizException("写入类型错误");
        }
        String path = "/" + router.routerType + router.path + "/" + router.version;
        if (curatorFramework.checkExists().forPath(path) == null) {
            curatorFramework.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, JsonUtil.obj2String(router).getBytes());
        } else {
            Logger.info("节点已存在: " + path);
        }
    }



    @Override
    public RouterEntity start(RouterService routerService) throws Exception {
        RouterEntity routerEntity= routerService.start();
        routerServiceMap.put(routerEntity,routerService);
        putRouter(routerEntity);
        return routerEntity;
    }

    @Override
    public void close(RouterEntity router) {
       RouterService routerService=  routerServiceMap.remove(router);
       routerService.close();
    }

    @Override
    public void restart(RouterEntity router) throws Exception {
        putRouter(router);
    }

    public RouterEntity getRouter(String name) {
        return routeTable.row(name).values().stream().findAny().get();
    }

    public Map<String, Map<String, RouterEntity>> getAllRouter() {
        return routeTable.columnMap();
    }


}
