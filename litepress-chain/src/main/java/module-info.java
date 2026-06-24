module litepress.chain{

    requires litepress.core;
    requires org.tinylog.api;
    requires java.sql;
    requires com.google.common;
    requires litepress.db;


    exports io.github.java_zengguang.litepress.chain.factory;
    exports io.github.java_zengguang.litepress.chain.drivers;
    exports io.github.java_zengguang.litepress.chain.components;
    exports io.github.java_zengguang.litepress.chain.util;
    exports io.github.java_zengguang.litepress.chain.exception;
    exports io.github.java_zengguang.litepress.chain.entity;


}