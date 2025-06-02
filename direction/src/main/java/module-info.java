open module litepress.direction {
    exports com.zg.litepress.direction.annotation;
    exports com.zg.litepress.direction.proxy;
    exports com.zg.litepress.direction.adapter;
    exports com.zg.litepress.direction.register;
    requires litepress.core;
    requires litepress.network;
    requires io.netty.all;
    requires zookeeper;
    requires com.google.common;
    requires curator.client;
    requires curator.framework;
    requires curator.recipes;
    requires io.github.javadiffutils;
    requires org.tinylog.api;
    requires io.netty.transport;
    requires jakarta.json;
    requires com.fasterxml.jackson.databind;

}