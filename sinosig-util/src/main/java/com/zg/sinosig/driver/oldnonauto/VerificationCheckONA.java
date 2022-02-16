package com.zg.sinosig.driver.oldnonauto;

import com.zg.sinosig.driver.check.VerificationCheck;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public class VerificationCheckONA extends VerificationCheck implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("old-non-auto".equals(systemFlag)) {
            checkSQL(list);
        }
        return list;
    }
}
