package com.zg.database.util;

import com.zg.bean.entity.OptionDB;
import com.zg.bean.factory.BeanFactory;
import com.zg.database.pool.C3p0;
import com.zg.database.pool.DataBaseInte;
import com.zg.database.pool.ZGDBP;

/**
 * Created by Administrator on 2018/12/17 0017.
 */
public class DBPUtils {

    private static DataBaseInte databasePool=null;


    private static boolean createDataBasePool(){
        OptionDB optionDB = (OptionDB) BeanFactory.createBean("optionDB");
        if ("C3p0".equals(optionDB.getDBPType())) {
            databasePool= C3p0.getInstance(optionDB);
        }else if ("ZGDBP".equals(optionDB.getDBPType())) {
            databasePool = ZGDBP.getInstance(optionDB);
        }else{
            System.out.println(JDBCUtils.class+"====没找到数据源");
            return false;
        }
        return true;
    }

    public static DataBaseInte getInstance(){
        if(databasePool==null){
            createDataBasePool();
        }
        return databasePool;
    }


}
