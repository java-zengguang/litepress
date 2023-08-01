module network.util {
    requires  io.netty.all;
    requires  java.logging;
    requires  common;
    exports com.zg.network.common.service;
    exports com.zg.network.common;
    exports com.zg.network.common.client;
    exports com.zg.network.bean;
    exports com.zg.network.common.fileservcie;
    exports com.zg.network.common.heartbeat;

    requires  org.tinylog.api;
}