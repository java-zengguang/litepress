package com.zg.sinosig.driver;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleAutoDriver implements AutoDriver {


    private List<String> getLinkClasses(String systemFlag) {
        List<String> list=new ArrayList<>();
        if ("new-non-auto".equals(systemFlag)) {
            String[] calssLine = {"com.zg.sinosig.driver.newnonauto.SimpleGenerateNNAUAT", "com.zg.sinosig.driver.newnonauto.CommonCheckSQLNNA", "com.zg.sinosig.driver.newnonauto.VerificationCheckNNA","com.zg.sinosig.driver.newnonauto.CheckTableRelationNNA", "com.zg.sinosig.driver.newnonauto.SimpleExecuteNNA","com.zg.sinosig.driver.newnonauto.SimpleLoadDataBaseStrucutreNNA"
                    ,"com.zg.sinosig.driver.newnonauto.SimpleGenerateNNASTAGE", "com.zg.sinosig.driver.newnonauto.CommonCheckSQLNNA", "com.zg.sinosig.driver.newnonauto.VerificationCheckNNA","com.zg.sinosig.driver.newnonauto.CheckTableRelationNNA","com.zg.sinosig.driver.newnonauto.SimpleExecuteNNA","com.zg.sinosig.driver.newnonauto.SimpleLoadDataBaseStrucutreNNA"};
            list=Arrays.asList(calssLine);
        }
        if ("old-non-auto".equals(systemFlag)) {
            //, "com.zg.sinosig.driver.oldnonauto.SimpleExecuteONA"
            String[] calssLine = {"com.zg.sinosig.driver.oldnonauto.SimpleGenerateONA", "com.zg.sinosig.driver.oldnonauto.CommonCheckSQLONA","com.zg.sinosig.driver.oldnonauto.VerificationCheckONA"};
            list=Arrays.asList(calssLine);
        }
        if ("platform".equals(systemFlag)) {
            String[] calssLine = {"com.zg.sinosig.driver.platform.SimpleGeneratePF", "com.zg.sinosig.driver.platform.CommonCheckSQLPF","com.zg.sinosig.driver.platform.VerificationCheckPF", "com.zg.sinosig.driver.platform.SimpleExecutePF","com.zg.sinosig.driver.platform.SynNewNonAuto"};
            list=Arrays.asList(calssLine);
        }

        return list;
    }

    private List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {

        List<String> linkClassesList=getLinkClasses(systemFlag);
        for(String classeName:linkClassesList){
            Class classes=Class.forName(classeName);
            SunAutoDriver autoDriver= (SunAutoDriver) classes.newInstance();
            list= autoDriver.doExecute(systemFlag,list);
        }

        return list;
    }

    @Override
    public List<SinoSigSQLLogEntity> doStart(List<SinoSigSQLLogEntity> list) throws Exception {
        List<SinoSigSQLLogEntity> resultList=new ArrayList<>();
        //先分组，保证后续流程，数据完整性
        Map<String, List<SinoSigSQLLogEntity>> batchMap = list.stream().collect(Collectors.groupingBy(SinoSigSQLLogEntity::getBatchno));
        Set<String> batchSet = batchMap.keySet();
        for (String batchNo : batchSet) {
            List<SinoSigSQLLogEntity> batchSinoSigSQLLogEntitys = batchMap.get(batchNo);
            Map<String, List<SinoSigSQLLogEntity>> systemFlagMap = batchSinoSigSQLLogEntitys.stream().collect(Collectors.groupingBy(SinoSigSQLLogEntity::getSystemflag));
            Set<String> systemFlagSet = systemFlagMap.keySet();
            for (String systemFlag : systemFlagSet) {
                List<SinoSigSQLLogEntity> systemFlagSinoSigSQLLogEntitys = systemFlagMap.get(systemFlag);
                systemFlagSinoSigSQLLogEntitys=doExecute(systemFlag, systemFlagSinoSigSQLLogEntitys);
                resultList.addAll(systemFlagSinoSigSQLLogEntitys);  //返回结果
            }
        }

        return resultList;

    }
}
