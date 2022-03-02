package com.zg.database.pool;

import java.sql.Connection;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public interface DataBaseInte {


    Connection getConnection();

    Connection getConnection(String dataSource);

}
