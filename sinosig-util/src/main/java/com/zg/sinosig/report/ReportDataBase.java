package com.zg.sinosig.report;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;
import java.util.Map;

public interface ReportDataBase  {
    void doResult(List<SinoSigSQLLogEntity> list);
}
