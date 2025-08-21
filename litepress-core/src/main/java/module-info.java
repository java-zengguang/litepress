module litepress.core {
    exports io.github.java_zengguang.litepress.core.util.reflect;
    exports io.github.java_zengguang.litepress.core.bean.entity;
    exports io.github.java_zengguang.litepress.core.util;
    exports io.github.java_zengguang.litepress.core.init;

    exports io.github.java_zengguang.litepress.core.bean.factory;
    exports io.github.java_zengguang.litepress.core.annotation;


    exports io.github.java_zengguang.litepress.core.util.url;
    exports io.github.java_zengguang.litepress.core.service;

    exports io.github.java_zengguang.litepress.core.error;
    exports io.github.java_zengguang.litepress.core.util.lock;
    exports io.github.java_zengguang.litepress.core.bean.handle;
    exports io.github.java_zengguang.litepress.core.relect.dynameic;

    exports io.github.java_zengguang.litepress.core.util.io.poi;

    requires commons.collections;
    requires java.sql;
    requires dom4j;
    requires java.compiler;
    requires cglib;

    requires com.esotericsoftware.kryo;
    requires java.naming;
    requires org.tinylog.api;
    requires com.fasterxml.jackson.databind;

    requires zip4j;
    requires net.bytebuddy;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


}