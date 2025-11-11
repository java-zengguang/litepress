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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class PoolConnectionFactory implements ConnectionFactory {

    private static final Map<String, DataSource> dataSourceMap = new Hashtable<>();

    private static PoolConnectionFactory poolConnectionFactory;

    private PoolConnectionFactory() {
    }

    public synchronized static PoolConnectionFactory getInstance() {
        if (poolConnectionFactory == null) {
            poolConnectionFactory = new PoolConnectionFactory();
        }
        return poolConnectionFactory;
    }


    @Override
    public Connection createConnection(String dataSourceName) throws Exception {
        DataSource dataSource = dataSourceMap.get(dataSourceName);
        if (dataSource == null) {
            OptionDB optionDB = (OptionDB) Config.getConfig(dataSourceName);
            if ("Druid".equals(optionDB.getDbptype())) {
                DataBasePool databasePool = DruidImpl.getInstance();
                dataSource = databasePool.createDataSource(optionDB);
            } else if ("HikariCP".equals(optionDB.getDbptype())) {
                DataBasePool databasePool = HikariCPImpl.getInstance();
                dataSource = databasePool.createDataSource(optionDB);
                HikariPoolMXBean pool = ((HikariDataSource) dataSource).getHikariPoolMXBean();
                Logger.info("当前活动连接数：" + pool.getActiveConnections() + " 当前空闲连接数：" + pool.getIdleConnections() + " 当前总连接数：" + pool.getTotalConnections() + " 当前等待获取连接的线程数：" + pool.getThreadsAwaitingConnection());

            } else {
                throw new BizException("未找到适配的连接池，支持Druid、HikariCP请检查配置！");
            }
            dataSourceMap.put(dataSourceName, dataSource);
        }
        return dataSource.getConnection();
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
