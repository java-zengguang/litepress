package com.zg.sinosig.driver.check;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public interface CheckSQL {

    List<SinoSigSQLLogEntity> checkSQL(List<SinoSigSQLLogEntity> list) throws Exception;

}
