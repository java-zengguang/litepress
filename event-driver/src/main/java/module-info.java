open module event.driver {
    requires io.netty.all;
    requires rocketmq.client;
    requires rocketmq.remoting;
    requires rocketmq.acl;
    requires rocketmq.common;
    requires fastjson;
    requires commons.lang3;
    requires ssdb4j;
    requires commons.pool2;
    requires com.google.common;
    requires org.zeromq.jeromq;
    requires router;
    requires common;
    requires org.tinylog.api;
    requires commons.logging;

    exports com.zg.event.driver.event;
    exports com.zg.event.driver.subsriber;
    exports com.zg.event.driver.bus;
    exports com.zg.event.driver.event.rule;
    exports com.zg.event.driver.event.manager;
    exports com.zg.event.driver.en;
    exports com.zg.event.driver.event.action;
    exports com.zg.event.driver.exception;
    exports com.zg.event.driver.entity;

}