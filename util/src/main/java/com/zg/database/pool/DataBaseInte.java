package com.zg.database.pool;

import java.sql.Connection;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public interface DataBaseInte {

    boolean release(Connection conn);

    boolean commit(Connection conn);

    Connection getConnection();

}
