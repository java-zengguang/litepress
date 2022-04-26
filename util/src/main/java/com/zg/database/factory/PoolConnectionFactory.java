package com.zg.database.factory;

import com.zg.bean.entity.OptionDB;
import com.zg.database.pool.C3p0Impl;
import com.zg.database.pool.DataBaseInte;
import com.zg.database.pool.ZGDBPImpl;
import com.zg.init.Config;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class PoolConnectionFactory extends BaseConnectionFactory {
    private static Logger logger = Logger.getLogger(ConnectionFactory.class);
    private static PoolConnectionFactory poolConnectionFactory;

    private PoolConnectionFactory() {

    }

    public synchronized static PoolConnectionFactory getInstance() {
        if (poolConnectionFactory == null) {
            poolConnectionFactory = new PoolConnectionFactory();
        }
        return poolConnectionFactory;
    }

    private DataBaseInte createDataBasePool(String dataSource) {
        DataBaseInte databasePool = null;
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);

        if ("C3p0".equals(optionDB.getDBPType())) {
            databasePool = C3p0Impl.getInstance();
        } else if ("ZGDBP".equals(optionDB.getDBPType())) {
            databasePool = ZGDBPImpl.getInstance();
        } else if (optionDB.getDBPType() == null || "".equals(optionDB.getDBPType())) {
            logger.info("未使用链接池");
        } else {
            logger.info("未找到对应链接池");
        }
        return databasePool;
    }

    @Override
    public Connection createConnection(String dataSourceName) throws ClassNotFoundException, SQLException {
        DataBaseInte dataBaseInte = createDataBasePool(dataSourceName);
        return dataBaseInte.getConnection(dataSourceName);

    }
}
