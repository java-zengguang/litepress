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

    private  static Map<String,C3p0Impl> c3p0Map=new HashMap<>();
    private static DataSource dataSource = null;


    public  static C3p0Impl getInstance(String dataOptionName){
        C3p0Impl c3p0=c3p0Map.get(dataOptionName);
        if(c3p0==null){
            c3p0=new C3p0Impl(dataOptionName);
            c3p0Map.put(dataOptionName,c3p0);
        }
        return c3p0;
    }

    private C3p0Impl(String dataOptionName){
        this.dataSource=createCPDS(dataOptionName);
    }

    private  ComboPooledDataSource createCPDS(String dataOptionName){
        OptionDB optionDB= (OptionDB) Config.getConfig(dataOptionName);
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(optionDB.getDriver());// loads the driver
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(optionDB.getUrl());
        cpds.setUser(optionDB.getUsername());
        cpds.setPassword(optionDB.getPassword());
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);

        return cpds;
    }


    /**
     * 获取连接
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    @Override
    public boolean commit(Connection conn) {
        try {
            conn.commit();
            release(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean release(Connection conn) {

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        return true;
    }


}
