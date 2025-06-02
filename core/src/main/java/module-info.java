module litepress.core {
    exports com.zg.litepress.core.util.reflect;
    exports com.zg.litepress.core.bean.entity;
    exports com.zg.litepress.core.util;
    exports com.zg.litepress.core.init;

    exports com.zg.litepress.core.bean.factory;
    exports com.zg.litepress.core.annotation;


    exports com.zg.litepress.core.util.url;
    exports com.zg.litepress.core.service;

    exports com.zg.litepress.core.error;
    exports com.zg.litepress.core.util.lock;
    exports com.zg.litepress.core.bean.handle;
    exports com.zg.litepress.core.relect.dynameic;

    exports  com.zg.litepress.core.util.io.poi;

    requires commons.collections;
    requires java.sql;
    requires dom4j;
    requires java.compiler;
    requires cglib;

    requires com.esotericsoftware.kryo;
    requires druid;
    requires java.naming;
    requires com.zaxxer.hikari;
    requires org.tinylog.api;
    requires com.fasterxml.jackson.databind;
    requires jsqlparser;
    requires pagehelper;
    requires zip4j;
    requires org.graalvm.nativeimage;
    requires org.reflections;
    requires net.bytebuddy;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


}