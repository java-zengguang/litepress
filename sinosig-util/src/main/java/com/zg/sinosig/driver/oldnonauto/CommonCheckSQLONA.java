package com.zg.sinosig.driver.oldnonauto;

import com.zg.sinosig.driver.check.BaseCheckSQL;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public class CommonCheckSQLONA extends BaseCheckSQL implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if ("old-non-auto".equals(systemFlag)) {
             checkSQL(list);
        }
        return list;
    }

    @Override
    public boolean checkCustomSQLRule(SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        return true;
    }
}
