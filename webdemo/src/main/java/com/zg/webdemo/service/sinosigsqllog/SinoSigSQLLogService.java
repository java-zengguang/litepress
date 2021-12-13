package com.zg.webdemo.service.sinosigsqllog;

import com.zg.webdemo.entity.SinoSingSQLLogEntity;

import java.sql.SQLException;
import java.util.List;

public interface SinoSigSQLLogService {
    SinoSingSQLLogEntity insertSinoSingSQLLog(SinoSingSQLLogEntity sinoSingSQLLogEntity) throws SQLException, IllegalAccessException;
     boolean updateStateSinoSingSQLLog(SinoSingSQLLogEntity sinoSingSQLLogEntity) throws SQLException;
}
