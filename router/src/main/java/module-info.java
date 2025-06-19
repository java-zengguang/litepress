module litepress.router {
    requires litepress.core;
    requires curator.client;
    requires curator.framework;
    requires org.tinylog.api;
    requires zookeeper;
    requires curator.recipes;
    requires com.google.common;

    exports com.zg.litepress.router.entity;
    exports com.zg.litepress.router.register;
    exports com.zg.litepress.router.annotation;
}