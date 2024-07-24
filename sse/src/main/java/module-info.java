 module sse {
    exports com.zg.sse;
    exports com.zg.sse.event;
     exports com.zg.sse.entity;
     exports com.zg.sse.servlet;
    requires org.eclipse.jetty.servlets;
    requires org.tinylog.api;
    requires common;
     requires com.google.common;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.servlet;
}
