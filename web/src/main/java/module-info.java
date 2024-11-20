open module web {
    requires java.jwt;
    requires bcprov.ext.jdk16;
    requires sse;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.servlets;
    requires common;
    requires org.tinylog.api;
    requires commons.collections;
    exports com.zg.mvc.servlet;
    exports com.zg.mvc.adapter;
    exports com.zg.mvc.annotation.autowired;
    exports com.zg.mvc.annotation.controller;
    exports com.zg.mvc.annotation.service;
    exports com.zg.mvc.controller;
    exports com.zg.mvc.entity;
    exports com.zg.mvc.util;
    exports com.zg.mvc.intercept;


}