module chain.common {

    requires common;
    requires org.tinylog.api;
    requires java.sql;
    requires com.google.common;
    requires org.apache.groovy;
    requires jdk.incubator.foreign;


    exports com.zg.chain.common.factory;
    exports com.zg.chain.common;
    exports com.zg.chain.common.drivers;
    exports com.zg.chain.common.components;
    exports com.zg.chain.common.util;
    exports com.zg.chain.common.exception;
    exports com.zg.chain.common.entity;


}