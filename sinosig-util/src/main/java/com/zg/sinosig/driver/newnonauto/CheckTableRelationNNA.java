package com.zg.sinosig.driver.newnonauto;

import com.zg.handler.ProxyUtils;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckTableRelationNNA  implements SunAutoDriver {

    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures,reloadDataBaseTableStructures");


    private void checkSQL(List<SinoSigSQLLogEntity> list) throws SQLException {
        String errorMessage = "";
        String stageFlag="3";
        List<Map> checkResult = new ArrayList<>();
        checkResult = strcutureService.getCompareResult("test", "CT");
        if (checkResult != null && checkResult.size() > 1) {//里面自带表头
            errorMessage = errorMessage + "当前批次套表字段类型检查不通过;";
            errorMessage = errorMessage + checkResult;
            stageFlag = "-1";
        }
        checkResult = strcutureService.getCompareResult("test", "CTC");
        if (checkResult != null && checkResult.size() > 1) {//自带表头，所以>1
            errorMessage = errorMessage + "当前批次套表缺少字段检查不通过;";
            errorMessage = errorMessage + checkResult;
            stageFlag = "-1";
        }

        checkResult = strcutureService.getCompareResult("test", "CTT");
        if (checkResult != null && checkResult.size() > 1) {//自带表头，所以>1
            errorMessage = errorMessage + "套表缺失检查不通过;";
            errorMessage = errorMessage + checkResult;
            stageFlag = "-1";
        }

        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            sinoSigSQLLogEntity.setErrormassage(errorMessage);
            sinoSigSQLLogEntity.executestate = stageFlag;
        }
    }

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("new-non-auto".equals(systemFlag)) {
            List checkList=new ArrayList();
            for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
                if("3".equals(sinoSigSQLLogEntity.executestate)){
                    checkList.add(sinoSigSQLLogEntity);
                }
            }
            checkSQL(checkList);
        }
        return list;
    }
}
