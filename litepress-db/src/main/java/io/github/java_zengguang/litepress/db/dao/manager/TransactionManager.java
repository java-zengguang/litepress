package io.github.java_zengguang.litepress.db.dao.manager;

import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.db.dao.factory.ConnectionFactory;
import io.github.java_zengguang.litepress.db.dao.factory.PoolConnectionFactory;
import io.github.java_zengguang.litepress.core.init.Config;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库链接绑定线程，作为service层数据源事务管理的基础，统一获取数据库链接
 */
public class TransactionManager {

    private static final ThreadLocal<Map<String, Connection>> threadLocal = new ThreadLocal<>();


    public static synchronized Connection getConnection(String dataSource) throws SQLException, ClassNotFoundException {
        Logger.info("base服务层-链接获取-"+dataSource);
        Connection connection = null;

        Map<String, Connection> dataSourceMap = threadLocal.get();

        if (dataSourceMap == null) {
            dataSourceMap = new ConcurrentHashMap<>();
            threadLocal.set(dataSourceMap);
        }

        connection = dataSourceMap.get(dataSource);

        if (connection == null) {
            OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
            if (optionDB.getDbptype() == null || "".equals(optionDB.getDbptype())) {
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


    public static synchronized void release() throws SQLException {
        Logger.info("base服务层-链接释放-all");
        Map<String, Connection> dataSourceMap = threadLocal.get();

        if(dataSourceMap!=null) {

            List<Map.Entry<String, Connection>> removeList = new ArrayList<>(dataSourceMap.entrySet());
            for (Map.Entry<String, Connection> entry : removeList) {
                entry.getValue().close();
                dataSourceMap.remove(entry.getKey());
            }
        }

    }


    public static synchronized void commit() throws SQLException {
        Logger.info("base服务层-事务提交-all");
        Map<String, Connection> dataSourceMap = threadLocal.get();
        if(dataSourceMap!=null) {
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

    public static synchronized void commit(String dataSource) throws SQLException, ClassNotFoundException {
        Logger.info("base服务层-事务提交-"+dataSource);
        Connection conn = getConnection(dataSource);
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

    public static synchronized void release(String dataSource) throws SQLException, ClassNotFoundException {
        Logger.info("base服务层-链接释放-"+dataSource);
        Connection conn = getConnection(dataSource);
        conn.close();
        Map<String, Connection> dataSourceMap = threadLocal.get();
        if (dataSourceMap != null) {
            dataSourceMap.remove(dataSource);
        }
    }


}
