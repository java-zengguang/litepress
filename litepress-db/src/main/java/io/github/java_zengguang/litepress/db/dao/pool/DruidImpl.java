package io.github.java_zengguang.litepress.db.dao.pool;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import org.tinylog.Logger;

import javax.sql.DataSource;
import java.util.Properties;

public class DruidImpl implements DataBasePool {

    private static DataBasePool druid;

    private DruidImpl() {
    }

    public synchronized static DataBasePool getInstance() {
        if (druid == null) {
            druid = new DruidImpl();
        }
        return druid;
    }


    public DataSource createDataSource(OptionDB optionDB) throws Exception {
        Logger.info("使用Druid链接");
        Properties properties = new Properties();
        properties.put("driverClassName", optionDB.driver);
        properties.put("username", optionDB.username);
        properties.put("password", optionDB.password);
        properties.put("url", optionDB.url);
        properties.put("initialSize", "1");
        properties.put("minIdle", "1");
        properties.put("validationQuery", "SELECT 1 FROM DUAL");
        properties.put("init-sql", "SQL ALTER session set NLS_DATE_FORMAT='YYYY-MM-DD HH24:MI:SS'");
        properties.put("testWhileIdle", "true");
        properties.put("testOnBorrow", "false");
        properties.put("poolPreparedStatements", "false");
        properties.put("maxActive", "" + optionDB.maxPoolSize);
        properties.put("maxWait", "10000");
        properties.put("removeAbandoned", "true");
        properties.put("removeAbandonedTimeout", "600");
        properties.put("timeBetweenEvictionRunsMillis", "30000");
        properties.put("logAbandoned", "false");
        properties.put("filters", "stat,config,wall");
        properties.put("defaultAutoCommit", "false");
        //  properties.put("connectionProperties","config.decrypt="true";config.decrypt.key=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALbMDWRmnQ21QFC8P8m75xpc2CbY3lcwJxAo5TQtgMx0GBnmr2vvvtmmdKvvYfrdM+DLfpB5jtu00HX2vEMmzBsCAwEAAQ==");

        return DruidDataSourceFactory.createDataSource(properties);
    }

}
