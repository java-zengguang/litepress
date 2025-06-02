open module litepress.event {
    requires io.netty.all;
    requires rocketmq.client;
    requires rocketmq.remoting;
    requires rocketmq.acl;
    requires rocketmq.common;
    requires ssdb4j;
    requires commons.pool2;
    requires com.google.common;
    requires org.zeromq.jeromq;
    requires litepress.router;
    requires litepress.core;
    requires org.tinylog.api;
    requires commons.logging;
    requires litepress.reaction;

    exports com.zg.litepress.event.event;
    exports com.zg.litepress.event.subsriber;
    exports com.zg.litepress.event.bus;
    exports com.zg.litepress.event.event.rule;
    exports com.zg.litepress.event.event.manager;
    exports com.zg.litepress.event.en;
    exports com.zg.litepress.event.event.action;
    exports com.zg.litepress.event.exception;
    exports com.zg.litepress.event.entity;

}