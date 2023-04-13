package com.zg.common.dao.factory;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    Connection createConnection(String dataSourceName) throws ClassNotFoundException, SQLException;

}
