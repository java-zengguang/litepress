open module direction {
    exports com.zg.direction.annotation;
    exports com.zg.direction.proxy;
    exports com.zg.direction.adapter;
    exports com.zg.direction.register;
    requires common;
    requires network.util;
    requires fastjson;
    requires io.netty.all;
    requires zookeeper;
    requires guava;
    requires curator.client;
    requires curator.framework;
    requires curator.recipes;
    requires io.github.javadiffutils;
    requires org.tinylog.api;
    requires io.netty.transport;

}