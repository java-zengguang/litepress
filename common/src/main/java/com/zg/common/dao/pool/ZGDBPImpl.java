package com.zg.common.dao.pool;

import com.zg.common.bean.entity.OptionDB;
import com.zg.common.init.Config;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ZGDBPImpl implements DataBaseInte {

    private final static ZGDBPImpl zGDBP = new ZGDBPImpl();
    private static Map<String, ZGDBPDataSource> dataSourceMap = new HashMap<>();

    private ZGDBPImpl() {

    }

    public static ZGDBPImpl getInstance() {
        return zGDBP;
    }

    private ZGDBPDataSource createCPDS(String dataOptionName) {
        OptionDB optionDB = (OptionDB) Config.getConfig(dataOptionName);


        ZGDBPDataSource cpds = ZGDBPDataSource.getInstance(optionDB);

        return cpds;
    }


    private ZGDBPDataSource getDataSource(String dataSourceName) {
        ZGDBPDataSource dataSource = dataSourceMap.get(dataSourceName);
        if (dataSource == null) {
            dataSource = createCPDS(dataSourceName);
            dataSourceMap.put(dataSourceName, dataSource);
        }
        return dataSource;
    }

    /**
     * 获取连接
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() {
        return getConnection("OpthinDB");
    }

    @Override
    public Connection getConnection(String dataSourceName) {
        Connection connection = null;
        try {
            ZGDBPDataSource dataSource = getDataSource(dataSourceName);
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            Logger.error(e);
        }
        return connection;
    }

}
