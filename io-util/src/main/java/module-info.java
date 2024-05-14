module io.util {
    requires org.apache.commons.net;
    requires org.tinylog.api;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    exports com.zg.io.poi;
    exports com.zg.io.ftp;
    exports com.zg.io.nio;
}