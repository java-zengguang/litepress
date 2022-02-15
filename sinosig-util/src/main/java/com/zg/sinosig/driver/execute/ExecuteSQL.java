package com.zg.sinosig.driver.execute;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.sql.SQLException;
import java.util.List;

public interface ExecuteSQL {

    List<SinoSigSQLLogEntity> excute(List<SinoSigSQLLogEntity> list) throws SQLException;

}
