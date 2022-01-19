package com.zg.sinosig.excute;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.sql.SQLException;
import java.util.List;

public interface ExcuteSQL {

    List<SinoSigSQLLogEntity> excute(List<SinoSigSQLLogEntity> list) throws SQLException;

}
