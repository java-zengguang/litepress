package com.zg.sinosig.driver.newnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.check.VerificationCheck;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.*;

public class VerificationCheckNNA extends VerificationCheck implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("new-non-auto".equals(systemFlag)) {
            checkSQL(list);
        }
        return list;
    }
}
