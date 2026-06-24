module litepress.db {
    requires cglib;
    requires com.esotericsoftware.kryo;
    requires com.zaxxer.hikari;
    requires druid;
    requires java.sql;
    requires litepress.core;
    requires org.tinylog.api;
    requires jsqlparser;
    requires pagehelper;
    requires org.apache.poi.poi;

    exports io.github.java_zengguang.litepress.db.proxy;
    exports io.github.java_zengguang.litepress.db.dao.assemble;
    exports io.github.java_zengguang.litepress.db.dao.manager;
    exports io.github.java_zengguang.litepress.db.dao.factory;
    exports io.github.java_zengguang.litepress.db.dao.pool;
    exports io.github.java_zengguang.litepress.db.dao.template;

    exports io.github.java_zengguang.litepress.db.handler;
    exports io.github.java_zengguang.litepress.db.util;
    exports io.github.java_zengguang.litepress.db.dao.dao;

}