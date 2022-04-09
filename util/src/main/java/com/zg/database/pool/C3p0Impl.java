package com.zg.database.pool;

/**
 * Created by zengguang on 2018/11/27 0027.
 */

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zg.bean.entity.OptionDB;
import com.zg.init.Config;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class C3p0Impl implements DataBaseInte {

    private static Map<String, DataSource> dataSourceMap = new HashMap<>();
    private final static C3p0Impl c3p0 = new C3p0Impl();

    private C3p0Impl() {

    }

    public static C3p0Impl getInstance() {
        return c3p0;
    }

    private ComboPooledDataSource createCPDS(String dataOptionName) {
        OptionDB optionDB = (OptionDB) Config.getConfig(dataOptionName);
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(optionDB.getDriver());// loads the driver
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(optionDB.getUrl());
        cpds.setUser(optionDB.getUsername());
        cpds.setPassword(optionDB.getPassword());

        cpds.setInitialPoolSize(2);
        cpds.setMinPoolSize(2);
        cpds.setMaxPoolSize(15);
        cpds.setAcquireIncrement(2);
        cpds.setAcquireRetryDelay(1000);
        cpds.setMaxIdleTime(3600);
        cpds.setTestConnectionOnCheckout(true);
        cpds.setIdleConnectionTestPeriod(10);
        cpds.setMaxStatements(0);
        cpds.setMaxStatementsPerConnection(0);
        cpds.setPreferredTestQuery(" select 1 ");
        cpds.setIdleConnectionTestPeriod(1800);
        cpds.setTestConnectionOnCheckout(false);

        return cpds;
    }


    private DataSource getDataSource(String dataSourceName) {
        DataSource dataSource = dataSourceMap.get(dataSourceName);
        if (dataSource == null) {
            dataSource = createCPDS(dataSourceName);
            dataSourceMap.put(dataSourceName, dataSource);
        }
        return dataSource;
    }

    /**
     * 获取连接
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() {
        return getConnection("optionDB");
    }

    @Override
    public Connection getConnection(String dataSourceName) {
        Connection connection = null;
        try {
            DataSource dataSource = getDataSource(dataSourceName);
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


}
