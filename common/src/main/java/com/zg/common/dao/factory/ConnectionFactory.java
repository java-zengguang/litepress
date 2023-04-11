package com.zg.common.dao.factory;

import com.zg.common.bean.entity.OptionDB;
import com.zg.common.init.Config;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory  {

    Connection createConnection(String dataSourceName) throws ClassNotFoundException, SQLException;

}
