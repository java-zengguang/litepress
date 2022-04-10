package com.zg.database.util;

import com.zg.database.factory.ConnectionFactory;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/17 0017.
 */
public class NewDBPUtils {

    private static Logger logger = Logger.getLogger(NewDBPUtils.class);
    private static ThreadLocal<Map<String, Connection>> threadLocal = new ThreadLocal();


    public static Connection getConnection(String dataSource) throws SQLException, ClassNotFoundException {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        Connection connection = null;

        Map<String, Connection> dataSourceMap = threadLocal.get();
        if (dataSourceMap == null) {
            dataSourceMap = new HashMap<>();
            connection = factory.createConnection(dataSource);
            dataSourceMap.put(dataSource, connection);
            threadLocal.set(dataSourceMap);

        } else {
            connection = dataSourceMap.get(dataSource);
            if (connection == null) {
                connection = factory.createConnection(dataSource);
                dataSourceMap.put(dataSource, connection);
            }
        }
        return connection;
    }


    public static boolean commit(String dataSource) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection(dataSource);
        try {
            if (!conn.getAutoCommit()) {
                conn.commit();
                release(dataSource);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
