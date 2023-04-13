package com.zg.common.dao.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zg.common.bean.entity.OptionDB;
import com.zg.common.init.Config;
import org.tinylog.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Map;

public class HikariCPImpl implements DataBaseInte {

    private final static DataBaseInte hikarCP = new HikariCPImpl();

    private static final Map<String, DataSource> dataSourceMap = new Hashtable();

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
                dataSource = createDataSourece(dataSourceName);
                dataSourceMap.put(dataSourceName, dataSource);
            }
            connection = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("数据库链接获取失败" + e.getMessage());
        }

        return connection;
    }

    private DataSource createDataSourece(String dataSourceName) throws Exception {
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
        config.setMaximumPoolSize(9);
/*        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");*/

        DataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

}
