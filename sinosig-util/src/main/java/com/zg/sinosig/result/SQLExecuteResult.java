package com.zg.sinosig.result;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public interface SQLExecuteResult {
     void doResult(List<SinoSigSQLLogEntity> list);
}
