package io.github.java_zengguang.litepress.db.dao.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;

import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.Map;

public class HikariCPImpl implements DataBasePool {

    private static DataBasePool hikarCP;

    private static final Map<String, DataSource> dataSourceMap = new Hashtable<>();

    private HikariCPImpl() {
    }


    public synchronized static DataBasePool getInstance() {
        hikarCP = new HikariCPImpl();
        return hikarCP;
    }



    public DataSource createDataSource(OptionDB optionDB) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(optionDB.url);
        config.setUsername(optionDB.username);
        config.setPassword(optionDB.password);
        config.setDriverClassName(optionDB.driver);
        config.setKeepaliveTime(60000);
        config.setMaxLifetime(1500000);
        config.setValidationTimeout(5000);
        config.setConnectionTestQuery("SELECT 1 from  dual");
        config.setMaximumPoolSize(optionDB.maxPoolSize);
        config.setMinimumIdle(1);
        config.setAutoCommit(false);
        config.setIdleTimeout(600000);
        config.setConnectionTimeout(30000); // 建议设置30秒超时
        return new HikariDataSource(config);
    }

}
