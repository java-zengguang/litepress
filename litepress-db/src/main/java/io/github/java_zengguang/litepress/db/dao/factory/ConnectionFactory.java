package io.github.java_zengguang.litepress.db.dao.factory;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    Connection createConnection(String dataSourceName) throws ClassNotFoundException, SQLException;

}
