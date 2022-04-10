package com.zg.database.factory;

import com.zg.bean.entity.OptionDB;
import com.zg.database.util.NewDBPUtils;
import com.zg.init.Config;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnectionFactory extends BaseConnectionFactory{

    private static Logger logger = Logger.getLogger(NewDBPUtils.class);
    private static JDBCConnectionFactory jdbcConnectionFactory;

    private JDBCConnectionFactory(){

    }

    public synchronized static JDBCConnectionFactory getInstance(){
        if(jdbcConnectionFactory==null){
            jdbcConnectionFactory=new JDBCConnectionFactory();
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
