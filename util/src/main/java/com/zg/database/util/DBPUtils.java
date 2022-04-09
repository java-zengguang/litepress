package com.zg.database.util;

import com.zg.bean.entity.OptionDB;
import com.zg.bean.factory.BeanFactory;
import com.zg.database.pool.C3p0Impl;
import com.zg.database.pool.DataBaseInte;
import com.zg.database.pool.ZGDBPImpl;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/17 0017.
 */
public class DBPUtils {

    private static Map<String, DataBaseInte> dataBaseInteMap = new HashMap<>();
    private static Logger logger = Logger.getLogger(DBPUtils.class);

    private static DataBaseInte createDataBasePool(String dataSource) {
        DataBaseInte databasePool = null;
        OptionDB optionDB = (OptionDB) BeanFactory.createBean(dataSource);
        if ("C3p0".equals(optionDB.getDBPType())) {
            databasePool = C3p0Impl.getInstance();
        } else if ("ZGDBP".equals(optionDB.getDBPType())) {
            databasePool = ZGDBPImpl.getInstance();
        } else if (optionDB.getDBPType() == null || "".equals(optionDB.getDBPType())) {
            logger.info("未使用链接池");
        } else {
            logger.info("未找到对应链接池");
        }
        dataBaseInteMap.put(dataSource, databasePool);
        return databasePool;
    }

    public static DataBaseInte getInstance() {

        return getInstance("optionDB");
    }

    public static DataBaseInte getInstance(String dataSource) {
        DataBaseInte databasePool = dataBaseInteMap.get(dataSource);
        if (databasePool == null) {
            databasePool = createDataBasePool(dataSource);

        }
        return databasePool;
    }

    public static Connection getConnection(String dataSource) {
        DataBaseInte databasePool = dataBaseInteMap.get(dataSource);
        if (databasePool == null) {
            databasePool = createDataBasePool(dataSource);

        }
        Connection connection = databasePool.getConnection();
        return connection;
    }


}
