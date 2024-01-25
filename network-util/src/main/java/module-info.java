module network.util {
    requires io.netty.all;
    requires java.logging;
    requires common;
    exports com.zg.network.common.service;
    exports com.zg.network.common.client;
    exports com.zg.network.bean;
    exports com.zg.network.common.fileservcie;
    exports com.zg.network.common.heartbeat;
    exports com.zg.network.common.cache;
    exports com.zg.network.entity;

    requires org.tinylog.api;
    requires guava;
    requires io.netty.transport;
    requires io.netty.codec;
    requires io.netty.common;
    requires io.netty.handler;
}