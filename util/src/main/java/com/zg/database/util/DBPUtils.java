package com.zg.database.util;

import com.zg.bean.entity.OptionDB;
import com.zg.bean.factory.BeanFactory;
import com.zg.database.pool.C3p0;
import com.zg.database.pool.DataBaseInte;
import com.zg.database.pool.ZGDBP;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/17 0017.
 */
public class DBPUtils {

    private static Map<String,DataBaseInte> dataBaseInteMap=new HashMap<>();


    private static DataBaseInte createDataBasePool(String dataSource){
        DataBaseInte databasePool=null;
        OptionDB optionDB = (OptionDB) BeanFactory.createBean(dataSource);
        if ("C3p0".equals(optionDB.getDBPType())) {
            databasePool= C3p0.getInstance(optionDB);
        }else if ("ZGDBP".equals(optionDB.getDBPType())) {
            databasePool = ZGDBP.getInstance(optionDB);
        }else{
            System.out.println(JDBCUtils.class+"====没找到数据源");
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




}
