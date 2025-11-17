module litepress.network {
    requires java.logging;
    requires litepress.core;
    exports io.github.java_zengguang.litepress.network.common.service;
    exports io.github.java_zengguang.litepress.network.common.client;

    exports io.github.java_zengguang.litepress.network.common.cache;
    exports io.github.java_zengguang.litepress.network.entity;

    requires org.tinylog.api;
    requires com.google.common;
    requires io.netty.transport;
    requires io.netty.codec;
    requires io.netty.common;
    requires io.netty.handler;
}