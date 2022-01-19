package com.zg.sinosing.excute;

import com.zg.webdemo.entity.SinoSingSQLLogEntity;

import java.sql.SQLException;
import java.util.List;

public interface ExcuteSQL {

    List<SinoSingSQLLogEntity> excute(List<SinoSingSQLLogEntity> list) throws SQLException;

}
