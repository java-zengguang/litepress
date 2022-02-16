package com.zg.sinosig.driver.oldnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.generate.SimpleGenerate;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import jnr.ffi.Struct;

import java.util.List;

public class SimpleGenerateONA extends SimpleGenerate implements SunAutoDriver {


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("old-non-auto".equals(systemFlag)) {
            for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
                sinoSigSQLLogEntity.environment="dev";
                if("投保单库".equals(sinoSigSQLLogEntity.databasename)){
                    sinoSigSQLLogEntity.owner="prpins";
                }
                if("保单库".equals(sinoSigSQLLogEntity.databasename)){
                    sinoSigSQLLogEntity.owner="sunshine";
                }
                sinoSigSQLLogEntity.environment = "old";
                sinoSigSQLLogEntity.executestate = "3";
                sinoSigSQLLogEntity.devsql=new String(sinoSigSQLLogEntity.basesql);
                sinoSigSQLLogEntity.prosql=new String(sinoSigSQLLogEntity.basesql);
            }
        }
        list=execute(list);
        return list;
    }

}
