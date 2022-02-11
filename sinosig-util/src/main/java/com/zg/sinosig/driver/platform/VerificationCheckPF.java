package com.zg.sinosig.driver.platform;

import com.zg.sinosig.check.VerificationCheck;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public class VerificationCheckPF extends VerificationCheck implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("platform".equals(systemFlag)) {
          //  checkSQL(list);
        }
        return list;
    }
}
