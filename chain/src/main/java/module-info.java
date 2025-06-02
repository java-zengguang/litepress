module litepress.chain{

    requires litepress.core;
    requires org.tinylog.api;
    requires java.sql;
    requires com.google.common;
    requires litepress.db;


    exports com.zg.litepress.chain.factory;
    exports com.zg.litepress.chain.drivers;
    exports com.zg.litepress.chain.components;
    exports com.zg.litepress.chain.util;
    exports com.zg.litepress.chain.exception;
    exports com.zg.litepress.chain.entity;


}