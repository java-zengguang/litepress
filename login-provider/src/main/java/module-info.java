module login.provider {
    exports com.zg.login.inte;
    requires  commons.collections;
    requires  java.sql;
    requires  common;
    requires  direction;
    requires  zookeeper;
    requires  org.tinylog.api;
}