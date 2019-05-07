package com.zg.database.pool;

/**
 * Created by zengguang on 2018/11/27 0027.
 */

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zg.bean.entity.OptionDB;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;


public class C3p0 implements DataBaseInte {

    private  static C3p0 c3p0=null;
    private static DataSource dataSource = null;


    public  static C3p0 getInstance(OptionDB optionDB){
        if(c3p0==null){
            c3p0=new C3p0(optionDB);
        }
        return c3p0;
    }

    private C3p0(OptionDB optionDB){
        this.dataSource=createCPDS(optionDB);
    }

    private  ComboPooledDataSource createCPDS(OptionDB optionDB){
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
