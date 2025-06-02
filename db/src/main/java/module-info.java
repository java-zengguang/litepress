module litepress.db {
    requires cglib;
    requires com.esotericsoftware.kryo;
    requires com.zaxxer.hikari;
    requires druid;
    requires java.sql;
    requires jsqlparser;
    requires litepress.core;
    requires org.tinylog.api;
    requires pagehelper;

    exports com.zg.litepress.db.proxy;
    exports com.zg.litepress.db.dao.assemble;
    exports com.zg.litepress.db.dao.database;
    exports com.zg.litepress.db.dao.factory;
    exports com.zg.litepress.db.dao.pool;
    exports com.zg.litepress.db.dao.template;

    exports com.zg.litepress.db.handler;
    exports com.zg.litepress.db.util;

}