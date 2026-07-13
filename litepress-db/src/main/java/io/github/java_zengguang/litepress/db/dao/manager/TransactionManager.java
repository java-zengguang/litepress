package io.github.java_zengguang.litepress.db.dao.manager;

import io.github.java_zengguang.litepress.db.dao.factory.ConnectionFactory;
import io.github.java_zengguang.litepress.db.dao.factory.PoolConnectionFactory;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TransactionManager {

    private static final ThreadLocal<Map<String, Connection>> threadLocal = new ThreadLocal<>();

    private static TransactionManager transactionManager;

    public synchronized static TransactionManager getInstance() {
        if (transactionManager == null) {
            transactionManager = new TransactionManager();
        }
        return transactionManager;
    }

    private TransactionManager() {
    }


    public Connection getConnection(String dataSource) throws Exception {
        Logger.debug("base服务层-链接获取-" + dataSource);
        Connection connection = null;
        Map<String, Connection> dataConnectionMap = threadLocal.get();

        if (dataConnectionMap == null) {
            dataConnectionMap = new ConcurrentHashMap<>();
            threadLocal.set(dataConnectionMap);
        }
        connection = dataConnectionMap.get(dataSource);
        if (connection == null) {
            ConnectionFactory factory = PoolConnectionFactory.getInstance();
            connection = factory.createConnection(dataSource);
            connection.setAutoCommit(false); // 关闭事务自动提交
            dataConnectionMap.put(dataSource, connection);
        }

        return connection;
    }


    public void release() throws SQLException {
        Logger.debug("base服务层-链接释放-all");
        Map<String, Connection> dataSourceMap = threadLocal.get();
        if (dataSourceMap != null) {
            List<Map.Entry<String, Connection>> removeList = new ArrayList<>(dataSourceMap.entrySet());
            for (Map.Entry<String, Connection> entry : removeList) {
                entry.getValue().close();
                dataSourceMap.remove(entry.getKey());
            }
        }

    }


    public void commit() throws SQLException {
        Logger.debug("base服务层-事务提交-all");
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


    public void release(String dataSource) throws Exception {
        Logger.debug("base服务层-链接释放-" + dataSource);
        Connection conn = getConnection(dataSource);
        conn.close();
        Map<String, Connection> dataSourceMap = threadLocal.get();
        if (dataSourceMap != null) {
            dataSourceMap.remove(dataSource);
        }
    }


}
