package com.zg.sinosig.driver;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public interface SunAutoDriver {
    List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception;
}
