package com.zg.common.dao.factory;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseConnectionFactory {
    public abstract Connection createConnection(String dataSourceName) throws ClassNotFoundException, SQLException;
}
