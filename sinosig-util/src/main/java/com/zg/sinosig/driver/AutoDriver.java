package com.zg.sinosig.driver;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public interface AutoDriver {
    List<SinoSigSQLLogEntity> doStart(List<SinoSigSQLLogEntity> list) throws Exception;
}
