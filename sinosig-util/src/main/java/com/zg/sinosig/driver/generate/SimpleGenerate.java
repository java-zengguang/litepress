package com.zg.sinosig.driver.generate;


import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public  class SimpleGenerate implements Generate {
    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog"));

    //重新加载uat已执行通过的数据
    public List<SinoSigSQLLogEntity> execute(List<SinoSigSQLLogEntity> list) throws SQLException, IllegalAccessException {

        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            if("3".equals(sinoSigSQLLogEntity.executestate)){
                sinoSigSQLLogService.insertSinoSingSQLLog(sinoSigSQLLogEntity);
            }
        }
        return list;
    }
}
