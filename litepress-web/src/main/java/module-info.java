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

    requires org.tinylog.api;
    requires commons.collections;
    requires com.google.common;
    requires litepress.core;
    requires reactor.core;
    requires io.netty.handler;
    requires java.net.http;

    exports io.github.java_zengguang.litepress.web.servlet;
    exports io.github.java_zengguang.litepress.web.servlet.adapter;
    exports io.github.java_zengguang.litepress.web.annotation.autowired;
    exports io.github.java_zengguang.litepress.web.annotation.controller;
    exports io.github.java_zengguang.litepress.web.annotation.service;
    exports io.github.java_zengguang.litepress.web.servlet.controller;
    exports io.github.java_zengguang.litepress.web.entity;
    exports io.github.java_zengguang.litepress.web.util;
    exports io.github.java_zengguang.litepress.web.servlet.intercept;
    exports io.github.java_zengguang.litepress.web.netty.adapter;
    exports io.github.java_zengguang.litepress.web.netty.reactor;
    exports io.github.java_zengguang.litepress.web.netty.intercepter;
    exports io.github.java_zengguang.litepress.web.netty.sse;
    exports io.github.java_zengguang.litepress.web.netty.reactor.request;
    exports io.github.java_zengguang.litepress.web.netty.reactor.response;

}