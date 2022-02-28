package com.zg.sinosig.driver.newnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.execute.SimpleExecute;
import com.zg.sinosig.load.SimpleLoadDataBaseStructure;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public class SimpleLoadDataBaseStrucutreNNA extends SimpleLoadDataBaseStructure implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("new-non-auto".equals(systemFlag)) {
            super.reLoadStructure(list);
        }
        return list;
    }
}
