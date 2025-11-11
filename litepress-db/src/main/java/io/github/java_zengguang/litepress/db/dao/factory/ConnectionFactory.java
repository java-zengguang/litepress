package io.github.java_zengguang.litepress.db.dao.factory;

import java.sql.Connection;
import java.util.List;

public interface ConnectionFactory {

    Connection createConnection(String dataSourceName) throws Exception;

    List<DBPoolStatusPo> getDBPoolState();
}
