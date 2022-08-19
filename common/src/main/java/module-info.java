open module common {
    exports com.zg.common.util.reflect;
    exports com.zg.common.bean.entity;
    exports com.zg.common.util;
    exports com.zg.common.init;
    exports com.zg.common.util.io;
    exports com.zg.common.bean.factory;
    exports com.zg.common.annotation;
    exports com.zg.common.dao.database;
    exports com.zg.common.handler;
    exports com.zg.common.proxy;
    exports com.zg.common.util.url;
    requires jxl;
    requires org.apache.poi.poi;
    requires commons.collections;
    requires java.sql;
    requires dom4j;
    requires fastjson;
    requires java.compiler;
    requires svnkit;
    requires cglib;
    requires org.apache.commons.net;
    requires org.slf4j;
    requires com.esotericsoftware.kryo;


}