open module direction {
    exports com.zg.direction.annotation;
    exports com.zg.direction.proxy;
    exports com.zg.direction.adapter;
    exports com.zg.direction.register;
    requires common;
    requires network.util;
    requires fastjson;
    requires io.netty.all;
    requires org.slf4j;

    requires zookeeper;

}