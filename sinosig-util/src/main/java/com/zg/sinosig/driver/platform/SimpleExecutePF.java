package com.zg.sinosig.driver.platform;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.execute.SimpleExecute;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public class SimpleExecutePF extends SimpleExecute implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("platform".equals(systemFlag)) {
            excute(list);
        }
        return list;
    }
}
