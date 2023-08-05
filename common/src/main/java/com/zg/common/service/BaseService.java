package com.zg.common.service;

import com.zg.common.bean.entity.OptionDB;
import com.zg.common.dao.factory.ConnectionFactory;
import com.zg.common.dao.factory.PoolConnectionFactory;
import com.zg.common.init.Config;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseService {
    private static ThreadLocal<Map<String, Connection>> threadLocal = new ThreadLocal();


    public synchronized Connection getConnection(String dataSource) throws SQLException, ClassNotFoundException {
        Connection connection = null;

        Map<String, Connection> dataSourceMap = threadLocal.get();

        if (dataSourceMap == null) {
            dataSourceMap = new HashMap<>();
            threadLocal.set(dataSourceMap);
        }

        connection = dataSourceMap.get(dataSource);

        if (connection == null) {
            OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
            if (optionDB.getDBPType() == null || "".equals(optionDB.getDBPType())) {
                Logger.info("使用JDBC链接");
                Class.forName(optionDB.driver);
                connection = DriverManager.getConnection(optionDB.url, optionDB.username, optionDB.password);
                connection.setAutoCommit(false);
            } else {
                //从连接池获取链接
                ConnectionFactory factory = PoolConnectionFactory.getInstance();
                connection = factory.createConnection(dataSource);
            }
            dataSourceMap.put(dataSource, connection);
        }


        return connection;
    }


    public synchronized  void release() throws SQLException, ClassNotFoundException {
        Map<String, Connection> dataSourceMap = threadLocal.get();

        if(dataSourceMap!=null) {
            List<Map.Entry<String, Connection>> removeList = new ArrayList<>();

            for (Map.Entry<String, Connection> entry : dataSourceMap.entrySet()) {
                removeList.add(entry);
            }
            for (Map.Entry<String, Connection> entry : removeList) {
                entry.getValue().close();
                dataSourceMap.remove(entry.getKey());
            }
        }

    }


    public synchronized   void commit() throws SQLException {
        Map<String, Connection> dataSourceMap = threadLocal.get();
        if (dataSourceMap != null) {
            List<Connection> connectionList = dataSourceMap.values().stream().toList();
            for (Connection conn : connectionList) {
                try {
                    if (!conn.getAutoCommit()) {
                        conn.commit();
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    Logger.error(e);
                    conn.rollback();
                }
            }
        }
    }
}
