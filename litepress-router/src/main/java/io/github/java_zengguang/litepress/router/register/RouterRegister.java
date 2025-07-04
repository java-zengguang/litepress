package io.github.java_zengguang.litepress.router.register;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.github.java_zengguang.litepress.core.error.BizException;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.router.annotation.ZKRegister;
import io.github.java_zengguang.litepress.router.entity.RouterEntity;
import io.github.java_zengguang.litepress.router.entity.RouterRegisterConfig;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RouterRegister<T extends RouterEntity> {

    private static Map<String, RouterRegister> registerMap = new HashMap<>();
    private Set<T> serviceSet = new HashSet<>();
    private Table<String, String, T> routeTable = HashBasedTable.create();  //name version  RouterEntity
    private  RouterRegisterConfig registerConfig ;
    private String namespace;
    private String routerType;
    private CuratorFramework curatorFramework;
    private Class<T> clazz;

    private RouterRegister( Class<T> clazz) throws Exception {
        ZKRegister zkRegister =clazz.getAnnotation(ZKRegister.class);
        registerConfig = (RouterRegisterConfig) Config.getConfig(zkRegister.name());
        this.clazz=  clazz;
        this.namespace= registerConfig.namespace;
        this.routerType = zkRegister.routerType();
        this.curatorFramework = getZkClient(routerType);

    }

    //这里开始提供API访问
    public static synchronized RouterRegister getInstance(Class<? extends RouterEntity> clazz) throws Exception {

        ZKRegister zkRegister = clazz.getAnnotation(ZKRegister.class);
        RouterRegister register = registerMap.get(zkRegister.name());
        if (register == null) {
            register = new RouterRegister(clazz);
            registerMap.put(zkRegister.name(), register);
        }
        return register;
    }


    private CuratorFramework getZkClient(String routerType) throws Exception {
        CuratorFramework zkClient = CuratorFrameworkFactory.builder().connectString(registerConfig.registerURL)
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
                    serviceSet.forEach((router) -> {
                        try {
                            this.putRouter(router);
                        } catch (Exception e) {
                            Logger.info(router.name + "重新注册失败！");
                        }
                    });
                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_ADDED) {
                    ChildData childData = treeCacheEvent.getData();
                    Logger.info("创建！" + childData.getPath());
                    if (childData.getData() != null && childData.getData().length > 0) {
                        T router = JsonUtil.string2Obj(new String(childData.getData()), clazz);
                        if (childData.getPath().equals("/" + router.routerType + router.path + "/" + router.version)) {
                            routeTable.put(router.name, router.version, router);
                        }

                    }
                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
                    ChildData childData = treeCacheEvent.getData();
                    Logger.info("修改！" + childData.getPath());
                    if (childData.getData() != null && childData.getData().length > 0) {
                        T router = JsonUtil.string2Obj(new String(childData.getData()), clazz);
                        if (childData.getPath().equals("/" + router.routerType + router.path + "/" + router.version)) {
                            routeTable.put(router.name, router.version, router);
                        }
                    }
                }
                if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED) {
                    ChildData childData = treeCacheEvent.getData();
                    Logger.info("删除！" + childData.getPath());

                    if (childData.getData() != null && childData.getData().length > 0) {
                        T router = JsonUtil.string2Obj(new String(childData.getData()), clazz);
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


        boolean initialized = countDownLatch.await(30, TimeUnit.SECONDS);
        if (!initialized) {
            Logger.error("Zookeeper 初始化超时！");
            throw new BizException("Zookeeper 初始化超时");
        }
        return zkClient;
    }


    private void saveRouter(T router) throws Exception {
        String path = "/" + router.routerType + router.path + "/" + router.version;
        if (curatorFramework.checkExists().forPath(path) == null) {
            curatorFramework.create()
                    .creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, JsonUtil.obj2String(router).getBytes());

        }else{
            curatorFramework.setData()
                    .forPath(path, JsonUtil.obj2String(router).getBytes());
        }
    }

    public void putRouter(T router) throws Exception {
        if (!routerType.equals(router.routerType)) {
            throw new BizException("写入类型错误");
        }
        router.state=1;
        saveRouter(router);
        serviceSet.add(router);
    }


    public T setState(String name,String version,Integer state) throws Exception {
       T router= routeTable.get(name,version);
       if(router==null) {
           T newRouter= JsonUtil.string2Obj(JsonUtil.obj2String(router),clazz);
           newRouter.state=state;
           saveRouter(newRouter);
           return newRouter;
       }
       return router;
    }

    public T getRouter(String name) {
        return routeTable.row(name).values().stream().filter((x)->x.state==1).findAny().get();
    }

    public List<T> getRouters(String name) {
        return routeTable.row(name).values().stream().toList();
    }


    public Map<String, Map<String, T>> getAllRouter() {
        return routeTable.columnMap();
    }


}
