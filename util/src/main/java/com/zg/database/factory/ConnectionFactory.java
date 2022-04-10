package com.zg.database.factory;

import com.zg.bean.entity.OptionDB;
import com.zg.database.pool.C3p0Impl;
import com.zg.database.pool.DataBaseInte;
import com.zg.database.pool.ZGDBPImpl;
import com.zg.init.Config;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory extends BaseConnectionFactory{

    private static Logger logger = Logger.getLogger(ConnectionFactory.class);
    public static ConnectionFactory connectionFactory;


    public synchronized static ConnectionFactory getInstance(){
        if(connectionFactory==null){
            connectionFactory=new ConnectionFactory();
        }
        return connectionFactory;
    }

    private ConnectionFactory(){

    }



    public Connection createConnection(String dataSource) throws ClassNotFoundException, SQLException {

        Connection connection;
        BaseConnectionFactory factory;


        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        if (optionDB.getDBPType() == null || "".equals(optionDB.getDBPType())) {
            factory =JDBCConnectionFactory.getInstance();
        } else {
            //从连接池获取链接
            factory =JDBCConnectionFactory.getInstance();
        }
        connection=factory.createConnection(dataSource);


        return connection;
    }

}
