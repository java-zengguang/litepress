package com.zg.sinosig.driver.oldnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.generate.SimpleGenerate;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

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
            sinoSigSQLLogEntity.environment="dev";
            if("投保单库".equals(sinoSigSQLLogEntity.databasename)){
                sinoSigSQLLogEntity.owner="prpins";
            }
            if("保单库".equals(sinoSigSQLLogEntity.databasename)){
                sinoSigSQLLogEntity.owner="sunshine";
            }
        }
        return list;
    }
}
