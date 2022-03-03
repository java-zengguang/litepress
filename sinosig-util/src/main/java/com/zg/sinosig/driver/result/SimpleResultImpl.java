package com.zg.sinosig.driver.result;

import com.sinosig.saab.util.DateUtil;
import com.sinosig.saab.util.FileUtils;
import com.zg.database.util.SvnUtil;
import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.util.io.POIUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class SimpleResultImpl implements Result, SunAutoDriver {

    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog"));


    @Override
    public void getResult(List<SinoSigSQLLogEntity> list) {
        try {
            for(SinoSigSQLLogEntity sinoSigSQLLogEntity:list){
                if("3".equals(sinoSigSQLLogEntity.executestate)) {
                    sinoSigSQLLogEntity.executestate = "1";
                    sinoSigSQLLogService.updateStateSinoSingSQLLog(sinoSigSQLLogEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        getResult(list);
       return list;
    }
}
