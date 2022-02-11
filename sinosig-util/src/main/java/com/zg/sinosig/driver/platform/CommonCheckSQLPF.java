package com.zg.sinosig.driver.platform;

import com.zg.sinosig.check.BaseCheckSQL;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public class CommonCheckSQLPF extends BaseCheckSQL implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if ("platform".equals(systemFlag)) {
             checkSQL(list);
        }
        return list;
    }

    @Override
    public boolean checkCustomSQLRule(SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        return true;
    }
}
