package com.zg.sinosig.driver.result;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public interface Result {
     void getResult(List<SinoSigSQLLogEntity> list);
}
