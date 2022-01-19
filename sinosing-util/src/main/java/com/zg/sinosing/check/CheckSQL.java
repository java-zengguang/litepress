package com.zg.sinosing.check;

import com.zg.webdemo.entity.SinoSingSQLLogEntity;

import java.sql.SQLException;
import java.util.List;

public interface CheckSQL {

    List<SinoSingSQLLogEntity> checkSQL(List<SinoSingSQLLogEntity> list) throws Exception;

}
