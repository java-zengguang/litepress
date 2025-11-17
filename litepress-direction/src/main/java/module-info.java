open module litepress.direction {
    exports io.github.java_zengguang.litepress.direction.annotation;
    exports io.github.java_zengguang.litepress.direction.proxy;
    exports io.github.java_zengguang.litepress.direction.adapter;
    exports io.github.java_zengguang.litepress.direction.register;
    exports io.github.java_zengguang.litepress.direction.util;

    requires litepress.core;
    requires litepress.network;
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
    requires io.netty.codec;

}