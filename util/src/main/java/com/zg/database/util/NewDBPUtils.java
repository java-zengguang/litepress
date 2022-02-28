package com.zg.database.util;

import com.zg.bean.entity.OptionDB;
import com.zg.bean.factory.BeanFactory;
import com.zg.database.pool.C3p0;
import com.zg.database.pool.C3p0Impl;
import com.zg.database.pool.DataBaseInte;
import com.zg.database.pool.ZGDBP;
import com.zg.init.Config;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/17 0017.
 */
public class NewDBPUtils {

    private static Logger logger = Logger.getLogger(NewDBPUtils.class);
    private static Map<String,DataBaseInte> dataBaseInteMap=new HashMap<>();


    private static DataBaseInte createDataBasePool(String dataSource){
        DataBaseInte databasePool=null;
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        if ("C3p0".equals(optionDB.getDBPType())) {
            databasePool= C3p0Impl.getInstance(dataSource);
        }else if ("ZGDBP".equals(optionDB.getDBPType())) {
            databasePool = ZGDBP.getInstance(optionDB);
        }else if (optionDB.getDBPType()==null||"".equals(optionDB.getDBPType())) {
            logger.info("未使用链接池");
        }else{
            logger.info("未找到对应链接池");
        }
        dataBaseInteMap.put(dataSource,databasePool);
        return databasePool;
    }

    public static DataBaseInte getInstance(){

        return getInstance("optionDB");
    }

    public static DataBaseInte getInstance(String dataSource){
        DataBaseInte databasePool=dataBaseInteMap.get(dataSource);
        if(databasePool==null){
          databasePool=  createDataBasePool(dataSource);

        }
        return databasePool;
    }


    public static Connection getConnection(String dataSource) throws SQLException, ClassNotFoundException {
        Connection connection;
        DataBaseInte databasePool=dataBaseInteMap.get(dataSource);
        if(databasePool==null){
            databasePool=  createDataBasePool(dataSource);
        }
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        if(optionDB.getDBPType()==null||"".equals(optionDB.getDBPType())){
            logger.info("使用JDBC链接");
            Class.forName(optionDB.driver);
            connection = DriverManager.getConnection(optionDB.url, optionDB.username, optionDB.password);
            connection.setAutoCommit(false);
        }else{
           connection= databasePool.getConnection();
        }

        return connection;
    }


}
