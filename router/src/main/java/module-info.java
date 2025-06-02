module router {
    requires common;
    requires curator.client;
    requires curator.framework;
    requires org.tinylog.api;
    requires zookeeper;
    requires curator.recipes;
    requires com.google.common;

    exports com.zg.router.entity;
    exports com.zg.router.register;
}