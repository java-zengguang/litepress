package com.zg.common.dao.database;

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

/**
 * Created by Administrator on 2018/12/17 0017.
 */
public class NewDBPUtils {

    private static final ThreadLocal<Map<String, Connection>> threadLocal = new ThreadLocal();


    public static Connection getConnection(String dataSource) throws SQLException, ClassNotFoundException {
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


    public static void release() throws SQLException, ClassNotFoundException {
        Map<String, Connection> dataSourceMap = threadLocal.get();

        List<Map.Entry<String, Connection>> removeList = new ArrayList<>();

        for (Map.Entry<String, Connection> entry : dataSourceMap.entrySet()) {
            removeList.add(entry);
        }
        for (Map.Entry<String, Connection> entry : removeList) {
            entry.getValue().close();
            dataSourceMap.remove(entry.getKey());
        }

    }


    public static void commit() throws SQLException {
        Map<String, Connection> dataSourceMap = threadLocal.get();
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

    public static boolean commit(String dataSource) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection(dataSource);
        try {
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            Logger.error(e);
            conn.rollback();
            return false;
        }

        return true;
    }

    public static void release(String dataSource) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection(dataSource);
        conn.close();
        Map<String, Connection> dataSourceMap = threadLocal.get();
        if (dataSourceMap != null) {
            dataSourceMap.remove(dataSource);
        }
    }


}
