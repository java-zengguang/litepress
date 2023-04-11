package com.zg.common.dao.factory;

import com.zg.common.bean.entity.OptionDB;
import com.zg.common.dao.pool.DataBaseInte;
import com.zg.common.dao.pool.DruidImpl;
import com.zg.common.dao.pool.HikariCPImpl;
import com.zg.common.dao.pool.ZGDBPImpl;
import com.zg.common.init.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class PoolConnectionFactory extends BaseConnectionFactory {

    public  final Logger logger = LoggerFactory.getLogger(this.getClass());

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

        if ("ZGDBP".equals(optionDB.getDBPType())) {
            logger.info("使用ZGDBP链接");
            databasePool = ZGDBPImpl.getInstance();
        }else if("Druid".equals(optionDB.getDBPType())){
            databasePool= DruidImpl.getInstance();
            logger.info("使用Druid链接");
        } else if("HikariCP".equals(optionDB.getDBPType())){
            databasePool= HikariCPImpl.getInstance();
            logger.info("HikariCP");
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
