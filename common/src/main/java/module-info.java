module common {
    exports com.zg.common.util.reflect;
    exports com.zg.common.bean.entity;
    exports com.zg.common.util;
    exports com.zg.common.init;
    exports com.zg.common.util.io;
    exports com.zg.common.bean.factory;
    exports com.zg.common.annotation;
    exports com.zg.common.dao.database;
    exports com.zg.common.handler;
    exports com.zg.common.proxy;
    exports com.zg.common.util.url;
    exports com.zg.common.service;
    exports com.zg.common.util.database;
    exports com.zg.common.error;


    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires commons.collections;
    requires java.sql;
    requires dom4j;
    requires fastjson;
    requires java.compiler;
    requires cglib;
    requires org.apache.commons.net;
    requires com.esotericsoftware.kryo;
    requires druid;
    requires java.naming;
    requires com.zaxxer.hikari;
    requires org.tinylog.api;
    requires com.fasterxml.jackson.databind;
    requires jsqlparser;
    requires pagehelper;


}