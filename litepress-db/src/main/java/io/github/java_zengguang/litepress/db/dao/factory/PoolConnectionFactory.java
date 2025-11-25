package io.github.java_zengguang.litepress.db.dao.factory;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.core.error.BizException;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.db.dao.pool.DataBasePool;
import io.github.java_zengguang.litepress.db.dao.pool.DruidImpl;
import io.github.java_zengguang.litepress.db.dao.pool.HikariCPImpl;
import org.tinylog.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PoolConnectionFactory implements ConnectionFactory {

    private static final ConcurrentMap<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    private static final PoolConnectionFactory poolConnectionFactory = new PoolConnectionFactory();

    private PoolConnectionFactory() {
    }

    public static PoolConnectionFactory getInstance() {
        return poolConnectionFactory;
    }


    @Override
    public Connection createConnection(String dataSourceName) {
        try {
            DataSource dataSource = dataSourceMap.computeIfAbsent(dataSourceName,
                    key -> {
                        try {
                            return createDataSourceSafely(key);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            if (dataSource instanceof HikariDataSource hikariDataSource) {
                HikariPoolMXBean pool = (hikariDataSource).getHikariPoolMXBean();
                Logger.info(String.format("%s当前活动连接数：%d 当前空闲连接数：%d 当前总连接数：%d 当前等待获取连接的线程数：%d",
                        hikariDataSource.getPoolName(),
                        pool.getActiveConnections(),
                        pool.getIdleConnections(),
                        pool.getTotalConnections(),
                        pool.getThreadsAwaitingConnection()));
            }
            return dataSource.getConnection();
        } catch (Exception e) {
            Logger.error(e);
            throw new BizException(e);
        }

    }


    private DataSource createDataSourceSafely(String dataSourceName) throws Exception {
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSourceName);
        if (optionDB == null) {
            throw new BizException("未找到数据源配置: " + dataSourceName);
        }
        if ("Druid".equals(optionDB.getDbptype())) {
            DataBasePool databasePool = DruidImpl.getInstance();
            return databasePool.createDataSource(optionDB);
        } else if ("HikariCP".equals(optionDB.getDbptype())) {
            DataBasePool databasePool = HikariCPImpl.getInstance();
            return databasePool.createDataSource(optionDB);
        } else {
            throw new BizException("未找到适配的连接池，支持Druid、HikariCP请检查配置！");
        }

    }


    @Override
    public List<DBPoolStatusPo> getDBPoolState() {
        List<DBPoolStatusPo> dbPoolStatusPos = new ArrayList<>();
        dataSourceMap.forEach((name, dataSource) -> {
            if (dataSource instanceof HikariDataSource hikariDataSource) {
                HikariPoolMXBean pool = hikariDataSource.getHikariPoolMXBean();
                DBPoolStatusPo dbPoolStatusPo = new DBPoolStatusPo();
                dbPoolStatusPo.dbName = name;
                dbPoolStatusPo.max = hikariDataSource.getMaximumPoolSize();
                dbPoolStatusPo.active = pool.getActiveConnections();
                dbPoolStatusPo.idle = pool.getIdleConnections();
                dbPoolStatusPo.total = pool.getTotalConnections();
                dbPoolStatusPo.awaiting = pool.getThreadsAwaitingConnection();
                dbPoolStatusPos.add(dbPoolStatusPo);
            }
        });
        return dbPoolStatusPos;
    }


}
