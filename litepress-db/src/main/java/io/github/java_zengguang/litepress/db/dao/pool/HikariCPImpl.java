package io.github.java_zengguang.litepress.db.dao.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.core.init.Config;
import org.tinylog.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Map;

public class HikariCPImpl implements DataBaseInte {

    private final static DataBaseInte hikarCP = new HikariCPImpl();

    private static final Map<String, DataSource> dataSourceMap = new Hashtable<>();

    private HikariCPImpl() {
    }


    public static DataBaseInte getInstance() {
        return hikarCP;
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
                dataSource = createDataSource(dataSourceName);
                dataSourceMap.put(dataSourceName, dataSource);
            }
            if (dataSource != null) {
                HikariPoolMXBean pool =  pool = ((HikariDataSource) dataSource).getHikariPoolMXBean();
                Logger.info("当前活动连接数：" + pool.getActiveConnections() + " 当前空闲连接数：" + pool.getIdleConnections() + " 当前总连接数：" + pool.getTotalConnections() + " 当前等待获取连接的线程数：" + pool.getThreadsAwaitingConnection());
                connection = dataSource.getConnection();
            }

        } catch (Exception e) {
            Logger.error(e);
            Logger.error("数据库链接获取失败" + e.getMessage());
        }

        return connection;
    }

    private DataSource createDataSource(String dataSourceName) throws Exception {
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSourceName);
        if (optionDB == null) {
            return null;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(optionDB.url);
        config.setUsername(optionDB.username);
        config.setPassword(optionDB.password);
        config.setDriverClassName(optionDB.driver);
        config.setKeepaliveTime(60000);
        config.setMaxLifetime(1500000);
        config.setValidationTimeout(5000);
        config.setConnectionTestQuery("SELECT 1 from  dual");
        config.setMaximumPoolSize(optionDB.maxPoolSize);
        config.setMinimumIdle(1);
        config.setAutoCommit(false);
        config.setIdleTimeout(600000);
        return new HikariDataSource(config);
    }

}
