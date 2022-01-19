package com.zg.webdemo.service.sinosigsqllog;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.sql.SQLException;

public interface SinoSigSQLLogService {
    SinoSigSQLLogEntity insertSinoSingSQLLog(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws SQLException, IllegalAccessException;
     boolean updateStateSinoSingSQLLog(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws SQLException;
}
