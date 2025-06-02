open module web {
    requires java.jwt;
    requires bcprov.ext.jdk16;
    requires sse;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.servlets;
    requires network.util;
    requires io.netty.transport;
    requires io.netty.codec.http;
    requires io.netty.buffer;
    requires jdk.compiler;
    requires io.netty.common;
    requires event.driver;
    requires common;
    requires rocketmq.client;
    requires org.tinylog.api;
    requires commons.collections;
    requires com.google.common;
    exports com.zg.mvc.servlet;
    exports com.zg.mvc.adapter;
    exports com.zg.mvc.annotation.autowired;
    exports com.zg.mvc.annotation.controller;
    exports com.zg.mvc.annotation.service;
    exports com.zg.mvc.controller;
    exports com.zg.mvc.entity;
    exports com.zg.mvc.util;
    exports com.zg.mvc.intercept;
    exports com.zg.mvc.web.adapter;
    exports com.zg.mvc.web.reactor;
    exports com.zg.mvc.web.intercepter;
    exports com.zg.mvc.web.sse;

}