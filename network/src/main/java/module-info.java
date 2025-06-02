module litepress.network {
    requires io.netty.all;
    requires java.logging;
    requires litepress.core;
    exports com.zg.litepress.network.common.service;
    exports com.zg.litepress.network.common.client;
    exports com.zg.litepress.network.bean;
    exports com.zg.litepress.network.common.fileservcie;
    exports com.zg.litepress.network.common.heartbeat;
    exports com.zg.litepress.network.common.cache;
    exports com.zg.litepress.network.entity;

    requires org.tinylog.api;
    requires com.google.common;
    requires io.netty.transport;
    requires io.netty.codec;
    requires io.netty.common;
    requires io.netty.handler;
}