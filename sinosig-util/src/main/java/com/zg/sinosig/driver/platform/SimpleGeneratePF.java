package com.zg.sinosig.driver.platform;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.generate.SimpleGenerate;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public class SimpleGeneratePF extends SimpleGenerate implements SunAutoDriver {


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("platform".equals(systemFlag)) {
            for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
                sinoSigSQLLogEntity.devsql=sinoSigSQLLogEntity.basesql;
                sinoSigSQLLogEntity.prosql=sinoSigSQLLogEntity.basesql;
                sinoSigSQLLogEntity.owner="platform";
                sinoSigSQLLogEntity.environment="dev";
                sinoSigSQLLogEntity.executestate="3";
            }
        }
       list= execute(list);
        return list;
    }
}
