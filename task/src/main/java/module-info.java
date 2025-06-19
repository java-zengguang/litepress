module task {
    requires common;
    requires org.tinylog.api;
    requires quartz;
    requires druid;
    exports com.zg.task;
    exports com.zg.task.base;
    exports com.zg.task.entity;
}