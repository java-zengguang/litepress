package com.zg.webdemo.service.sinosigsqllog;

import com.zg.handler.CommitClassHandler;
import com.zg.webdemo.dao.DatabaseTableStructureMapper;
import com.zg.webdemo.dao.SinoSingSQLLogMapper;
import com.zg.webdemo.entity.SinoSingSQLLogEntity;

import java.sql.SQLException;
import java.util.List;

public class SinoSigSQLLogServiceImpl extends CommitClassHandler implements SinoSigSQLLogService{
    SinoSingSQLLogMapper mapper=new SinoSingSQLLogMapper();
    @Override
    public SinoSingSQLLogEntity insertSinoSingSQLLog(SinoSingSQLLogEntity sinoSingSQLLogEntity) throws SQLException, IllegalAccessException {
      return mapper.insertSinoSingSQLLog(sinoSingSQLLogEntity);
    }

    @Override
    public boolean updateStateSinoSingSQLLog(SinoSingSQLLogEntity sinoSingSQLLogEntity) throws SQLException {

        if (mapper.updateStateSinoSingSQLLog(sinoSingSQLLogEntity) > 0) {
            return true;
        } else {
            return false;
        }
    }

}
