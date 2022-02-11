package com.zg.sinosig.driver.oldnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.generate.SimpleGenerate;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.ArrayList;
import java.util.List;

public class SimpleGenerateONA extends SimpleGenerate implements SunAutoDriver {


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("old-non-auto".equals(systemFlag)) {
            execute(list);
        }
        return list;
    }


    @Override
    public List<SinoSigSQLLogEntity> execute(List<SinoSigSQLLogEntity> list) {
        for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
            sinoSigSQLLogEntity.environment="dev_old";
        }
        return list;
    }
}
