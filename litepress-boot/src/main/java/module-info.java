open module litepress.boot {
    requires dom4j;
    requires litepress.core;
    requires litepress.web;
    requires org.tinylog.api;
    requires io.github.classgraph;


    exports io.github.java_zengguang.litepress.boot.annotation;
    exports io.github.java_zengguang.litepress.boot.init;
    exports io.github.java_zengguang.litepress.boot;

}