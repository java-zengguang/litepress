package io.github.java_zengguang.litepress.db.dao.factory;

import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.db.dao.pool.DataBaseInte;
import io.github.java_zengguang.litepress.db.dao.pool.DruidImpl;
import io.github.java_zengguang.litepress.db.dao.pool.HikariCPImpl;
import io.github.java_zengguang.litepress.core.init.Config;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class PoolConnectionFactory extends BaseConnectionFactory {


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

       if ("Druid".equals(optionDB.getDbptype())) {
            databasePool = DruidImpl.getInstance();
            Logger.info("使用Druid链接");
        } else if ("HikariCP".equals(optionDB.getDbptype())) {
            databasePool = HikariCPImpl.getInstance();
            Logger.info("HikariCP");
        } else if (optionDB.getDbptype() == null || "".equals(optionDB.getDbptype())) {
            Logger.info("未使用链接池");
        } else {
            Logger.info("未找到对应链接池");
        }
        return databasePool;
    }

    @Override
    public Connection createConnection(String dataSourceName) throws ClassNotFoundException, SQLException {

        DataBaseInte dataBaseInte = createDataBasePool(dataSourceName);
        return dataBaseInte.getConnection(dataSourceName);

    }
}
