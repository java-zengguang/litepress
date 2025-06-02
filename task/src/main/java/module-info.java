module litepress.task {
    requires litepress.core;
    requires org.tinylog.api;
    requires quartz;
    requires druid;
    exports com.zg.litepress.task;
    exports com.zg.litepress.task.base;
    exports com.zg.litepress.task.entity;
}