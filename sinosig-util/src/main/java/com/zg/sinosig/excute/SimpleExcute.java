package com.zg.sinosig.excute;

import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleExcute implements ExcuteSQL{

    //private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyClass(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog");
    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(),"insertSinoSingSQLLog,updateStateSinoSingSQLLog"));

    @Override
    public List<SinoSigSQLLogEntity> excute(List<SinoSigSQLLogEntity> list) throws SQLException {

        for (SinoSigSQLLogEntity sinoSigSQLLogEntity :list){
            if("3".equals(sinoSigSQLLogEntity.executestate)) {
                ArrayList sqlList = new ArrayList();
                String dataSourceName = sinoSigSQLLogEntity.getDataSource();
                String sql = sinoSigSQLLogEntity.devsql;
                if (sql.contains(";")) {
                    String str[] = sql.split(";");
                    for (String s : str) {
                        s = s.trim();
                        if (s != null && !"".equals(s)) {
                            sqlList.add(s);
                        }
                    }
                } else {
                    sqlList.add(sql+";");
                }

                //获取数据库链接
                NewJDBCUtil jdbcUtil = new NewJDBCUtil(dataSourceName);
                if (jdbcUtil == null) {
                    sinoSigSQLLogEntity.executestate = "-1";
                    sinoSigSQLLogEntity.setErrormassage("数据库链接获取失败");
                }
                try {
                    jdbcUtil.batchSql(sqlList,true);
                    sinoSigSQLLogEntity.executestate = "3";
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    sinoSigSQLLogEntity.executestate = "-1";
                    sinoSigSQLLogEntity.errormassage = sinoSigSQLLogEntity.errormassage + throwables.getMessage();
                    continue;
                } finally {
                    jdbcUtil.commit();
                    sinoSigSQLLogService.updateStateSinoSingSQLLog(sinoSigSQLLogEntity);
                }
            }
        }

        return list;
    }
}
