package com.zg.common.dao.factory;

import com.zg.common.bean.entity.OptionDB;
import com.zg.common.dao.database.NewDBPUtils;
import com.zg.common.init.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnectionFactory extends BaseConnectionFactory {

    public  final Logger logger = LoggerFactory.getLogger(this.getClass());


    private static JDBCConnectionFactory jdbcConnectionFactory;

    private JDBCConnectionFactory() {

    }

    public synchronized static JDBCConnectionFactory getInstance() {
        if (jdbcConnectionFactory == null) {
            jdbcConnectionFactory = new JDBCConnectionFactory();
        }
        return jdbcConnectionFactory;
    }

    public Connection createConnection(String dataSource) throws ClassNotFoundException, SQLException {

        Connection connection;
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        logger.info("使用JDBC链接");
        Class.forName(optionDB.driver);
        connection = DriverManager.getConnection(optionDB.url, optionDB.username, optionDB.password);
        connection.setAutoCommit(false);

        return connection;
    }
}
