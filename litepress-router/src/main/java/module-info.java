module litepress.router {
    requires litepress.core;
    requires curator.client;
    requires curator.framework;
    requires org.tinylog.api;
    requires zookeeper;
    requires curator.recipes;
    requires com.google.common;

    exports io.github.java_zengguang.litepress.router.entity;
    exports io.github.java_zengguang.litepress.router.register;
    exports io.github.java_zengguang.litepress.router.annotation;
}