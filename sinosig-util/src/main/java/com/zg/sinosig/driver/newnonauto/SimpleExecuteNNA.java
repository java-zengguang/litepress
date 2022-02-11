package com.zg.sinosig.driver.newnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.execute.SimpleExecute;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public class SimpleExecuteNNA extends SimpleExecute implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("new-non-auto".equals(systemFlag)) {
            super.excute(list);
        }
        return list;
    }
}
