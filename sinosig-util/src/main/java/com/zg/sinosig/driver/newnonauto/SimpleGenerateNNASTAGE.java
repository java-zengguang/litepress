package com.zg.sinosig.driver.newnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.generate.SimpleGenerate;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.ArrayList;
import java.util.List;

public class SimpleGenerateNNASTAGE extends SimpleGenerate implements SunAutoDriver {


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        List<SinoSigSQLLogEntity> resultList = new ArrayList();
        if ("new-non-auto".equals(systemFlag)) {
            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                if (checkCustomModelRule(sinoSigSQLLogEntity)) {
                    SinoSigSQLLogEntity entity = machiningSQL(sinoSigSQLLogEntity);
                    resultList.add(entity);
                }
            }
        }
        resultList = execute(resultList);
        list.addAll(resultList);
        return list;
    }


    boolean checkCustomModelRule(SinoSigSQLLogEntity baseEntity) {
        if ("3".equals(baseEntity.executestate) || "1".equals(baseEntity.executestate)) {
            return true;
        } else {
            return false;
        }

    }

    //以入口为模板复制加工处新的对象
    private SinoSigSQLLogEntity machiningSQL(SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        sinoSigSQLLogEntity = (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
        sinoSigSQLLogEntity.executestate = "3";
        sinoSigSQLLogEntity.errormassage = "";
        sinoSigSQLLogEntity.environment = "stage";
        return sinoSigSQLLogEntity;
    }



}
