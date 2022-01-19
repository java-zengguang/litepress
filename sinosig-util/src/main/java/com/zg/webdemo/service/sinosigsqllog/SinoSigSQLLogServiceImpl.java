package com.zg.webdemo.service.sinosigsqllog;

import com.zg.handler.CommitClassHandler;
import com.zg.webdemo.dao.SinoSingSQLLogMapper;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.sql.SQLException;

public class SinoSigSQLLogServiceImpl extends CommitClassHandler implements SinoSigSQLLogService{
    SinoSingSQLLogMapper mapper=new SinoSingSQLLogMapper();
    @Override
    public SinoSigSQLLogEntity insertSinoSingSQLLog(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws SQLException, IllegalAccessException {
      return mapper.insertSinoSingSQLLog(sinoSigSQLLogEntity);
    }

    @Override
    public boolean updateStateSinoSingSQLLog(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws SQLException {

        if (mapper.updateStateSinoSingSQLLog(sinoSigSQLLogEntity) > 0) {
            return true;
        } else {
            return false;
        }
    }

}
