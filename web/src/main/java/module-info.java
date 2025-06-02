open module litepress.web {
    requires java.jwt;
    requires bcprov.ext.jdk16;

    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.servlets;
    requires litepress.network;
    requires io.netty.transport;
    requires io.netty.codec.http;
    requires io.netty.buffer;
    requires jdk.compiler;
    requires io.netty.common;

    requires rocketmq.client;
    requires org.tinylog.api;
    requires commons.collections;
    requires com.google.common;
    requires litepress.core;
    requires litepress.event;
    exports com.zg.litepress.web.servlet;
    exports com.zg.litepress.web.servlet.adapter;
    exports com.zg.litepress.web.annotation.autowired;
    exports com.zg.litepress.web.annotation.controller;
    exports com.zg.litepress.web.annotation.service;
    exports com.zg.litepress.web.servlet.controller;
    exports com.zg.litepress.web.entity;
    exports com.zg.litepress.web.util;
    exports com.zg.litepress.web.servlet.intercept;
    exports com.zg.litepress.web.netty.adapter;
    exports com.zg.litepress.web.netty.reactor;
    exports com.zg.litepress.web.netty.intercepter;
    exports com.zg.litepress.web.netty.sse;

}