package com.zg.sinosig.report;

import com.zg.sinosig.driver.SunAutoDriver;

import java.util.List;
import java.util.Map;

public interface ReportDataBase extends SunAutoDriver {
    Map<String, List> getReportDataBase();
}
