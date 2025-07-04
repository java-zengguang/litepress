package io.github.java_zengguang.litepress.db.dao.pool;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.core.init.Config;
import org.tinylog.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

public class DruidImpl implements DataBaseInte {

    private final static DataBaseInte druid = new DruidImpl();

    private static final Map<String, DataSource> dataSourceMap = new Hashtable<>();

    private DruidImpl() {
    }

    public static DataBaseInte getInstance() {
        return druid;
    }

    public static void main(String[] args) throws Exception {


    }

    @Override
    public Connection getConnection() {

        return getConnection("optionDB");
    }

    @Override
    public Connection getConnection(String dataSourceName) {
        Connection connection = null;
        try {
            DataSource dataSource = dataSourceMap.get(dataSourceName);
            if (dataSource == null) {
                dataSource = createDataSourece(dataSourceName);
                dataSourceMap.put(dataSourceName, dataSource);
            }
            if (dataSource != null) {
                connection = dataSource.getConnection();
            }
        } catch (Exception e) {
            Logger.error(e);
            Logger.error("数据库链接获取失败" + e.getMessage());
        }

        return connection;
    }

    private DataSource createDataSourece(String dataSourceName) throws Exception {
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSourceName);
        if (optionDB == null) {
            return null;
        }
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
        properties.put("maxActive", ""+optionDB.maxPoolSize);
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
