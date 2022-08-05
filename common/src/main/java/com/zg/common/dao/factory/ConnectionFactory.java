package com.zg.common.dao.factory;

import com.zg.common.bean.entity.OptionDB;
import com.zg.common.init.Config;


import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory extends BaseConnectionFactory {

    public static ConnectionFactory connectionFactory;


    private ConnectionFactory() {

    }

    public synchronized static ConnectionFactory getInstance() {
        if (connectionFactory == null) {
            connectionFactory = new ConnectionFactory();
        }
        return connectionFactory;
    }

    public Connection createConnection(String dataSource) throws ClassNotFoundException, SQLException {

        Connection connection;
        BaseConnectionFactory factory;


        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        if (optionDB.getDBPType() == null || "".equals(optionDB.getDBPType())) {
            factory = JDBCConnectionFactory.getInstance();
        } else {
            //从连接池获取链接
            factory = JDBCConnectionFactory.getInstance();
        }
        connection = factory.createConnection(dataSource);


        return connection;
    }

}
