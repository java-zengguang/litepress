module litepress.label {

    requires com.github.jknack.handlebars;
    requires easy.rules.core;
    requires litepress.core;
    requires org.tinylog.api;
    requires litepress.db;

    exports io.github.java_zengguang.litepress.label.sql;
    exports io.github.java_zengguang.litepress.label.rule;
    exports io.github.java_zengguang.litepress.label.entity;


}