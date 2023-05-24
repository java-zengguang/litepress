open  module web {
    requires common;
    requires commons.collections;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.servlets;
    requires org.tinylog.api;
    requires cacheonix.core;
    requires java.jwt;
    requires bcprov.ext.jdk16;
    exports com.zg.mvc.servlet;
    exports com.zg.mvc.adapter;
    exports com.zg.mvc.annotation.autowired;
    exports com.zg.mvc.annotation.controller;
    exports com.zg.mvc.annotation.service;
    exports com.zg.mvc.controller;
    exports com.zg.mvc.entity;
    exports com.zg.mvc.util;



}