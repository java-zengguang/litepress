package com.zg.sinosig.check;

import com.zg.database.util.JDBCUtils;
import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;
import com.zg.webdemo.service.ldcode.LDCodeService;
import com.zg.webdemo.service.ldcode.LDCodeServiceImpl;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class SimpleCheckSQL implements CheckSQL {

    //private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyClass(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog");
    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(),"insertSinoSingSQLLog,updateStateSinoSingSQLLog"));


    LDCodeService ldCodeService= (LDCodeService) ProxyUtils.getProxyClass(new LDCodeServiceImpl(),"reLoadPRPTable");

    static Logger logger = Logger.getLogger(SQLUtils.class.getName());


    public boolean checkSQLScript(SinoSigSQLLogEntity sinoSigSQLLogEntity) {

        String vsql = sinoSigSQLLogEntity.basesql;
        if (vsql == null || vsql.equals("")) {
            sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本不能为空！");
            return false;
        }

        if (true) {

            String sql = vsql.toLowerCase().trim() + "";

            if (sql == null || sql.equals("")) {
                sinoSigSQLLogEntity.setErrormassage("  检查未通过:不能使用外键，建议使用业务逻辑判断来代替外键!!");
                return false;
            }

            //非阻断
        if (sql.contains("database") || sql.contains("revoke") || sql.contains("flush") || sql.contains("truncate") || sql.contains("rename") || sql.contains("view") || sql.contains("function") || sql.contains("procedure") || sql.contains("drop")) {
            sinoSigSQLLogEntity.setErrormassage("请注意:包含数据库敏感关键字(database,revoke,flush,truncate,rename,view,function,procedure,drop)中的一个或多个，为防止误操作，请人工检查!!");
         //   return false;
        }

            if (sql.contains("sequence")) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:创建序列请注意!!");
                return false;
            }

            if (sql.contains("create table") && !(sql.contains("primary") && sql.contains("key"))) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:新创建表没有主键，表必须有主键!!");
                return false;
            }


            if (!sql.endsWith(";")) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本必须有结束符!!");
                return false;
            }

            if (true) {
                String[] sqlArray = sql.split(";");
                for (int i = 0; i < sqlArray.length - 1; i++) {
                    if (sqlArray[i].trim().length() < 1) {
                        sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本分割检查有误，请检查是否多了结算符!!");

                    }
                }
            }
        }

        if (true) {
            String xsql = vsql.toLowerCase().trim() + "";

            List<String> sqlList = new ArrayList();
            if (xsql.contains(";")) {
                String str[] = xsql.split(";");
                for (String s : str) {
                    s = s.trim();
                    if (s != null && !"".equals(s)) {
                        sqlList.add(s);
                    }
                }
            } else {
                sqlList.add(xsql);
            }

            for (String sql : sqlList) {
                sql = sql.toLowerCase();
                if (sql == null || "".equals(sql)) {
                    continue;
                }
                List<String> list = Arrays.asList(sql.split("\\s+"));
                if (list.contains("create") && !list.contains("synonym") || list.contains("alter")) {
                    String opreate = list.get(0);
                    String opreateObject = list.get(1);
                    String tableName = list.get(2);
                    String exeOpreate = list.get(3);
                    String columnName = list.get(4);
                    String columnType = list.get(5);
                    //去掉属主
                    if (opreateObject.contains(".")) {

                    }
                    if (tableName.contains(".")) {
                        tableName = tableName.substring(tableName.indexOf(".") + 1, tableName.length());
                    }
                    if ("table".equals(opreateObject)) {


                        String flag = JDBCUtils.getOneValue("select owner from databasetablestructure d where d.environment  in ('pro')  and d.tablename ='" + tableName + "'");
                        if ("".equals(flag) && "alter".equals(opreate) && !sql.contains("constraint")) {
                            sinoSigSQLLogEntity.setErrormassage("检查未通过:生产没有这个表 " + "tablename:" + tableName);
                            return false;
                        }
                        if (!"".equals(flag) && "create".equals(opreate)) {
                            sinoSigSQLLogEntity.setErrormassage("检查未通过:生产已经有这个表了 " + "tablename:" + tableName);
                            return false;
                        }
                        if (!"".equals(flag) && !"nvpolicy".equals(flag) && !"nvproposal".equals(flag) && !"nvendorsement".equals(flag)) {
                            sinoSigSQLLogEntity.setErrormassage("检查未通过:生产环境这个表归属不是承保系统 " + "tablename:" + tableName+" owner:"+flag);
                            return false;
                        }

                        if (("alter").equals(opreate)) {
                            if (("drop").equals(exeOpreate)) {
                                sinoSigSQLLogEntity.setErrormassage("检查未通过:单个脚本不允许 drop ");
                                return false;
                            }
                            if (("rename").equals(exeOpreate)) {
                                sinoSigSQLLogEntity.setErrormassage("检查未通过:单个脚本不允许 rename ");
                                return false;
                            }
                            if ("add".equals(exeOpreate)) {
                                String flag1 = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('pro') and d.databasename='"+ sinoSigSQLLogEntity.databasename+"' and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "' ");
                                if ("1".equals(flag1)) {
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:生产已有此字段" + "tablename:" + tableName + " columnname:" + columnName);
                                    return false;
                                }
                                flag1 = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('old','old_dev') and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "'  and d.columntype <> '" + columnType + "' ");
                                if ("1".equals(flag1)) {
                                    logger.info("检查未通过:老核心已有此字段，且字段类型或长度不一致" + "tablename:" + tableName + " columnname:" + columnName);
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:老核心已有此字段，且字段类型或长度不一致" + "tablename:" + tableName + " columnname:" + columnName);
                                   // return false;
                                }

                            }
                            if (("modify").equals(exeOpreate)) {

                                String oldColumnType = JDBCUtils.getOneValue("select d.columntype from databasetablestructure d where d.environment  in ('pro') and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "' ");
                                if ("".equals(oldColumnType)) {
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:生产不存在此字段" + "tablename:" + tableName + " columnname:" + columnName);
                                    return false;
                                }
                                if (!compareColumnType(oldColumnType, columnType, sinoSigSQLLogEntity)) {
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:字段类型校验失败" + "tablename:" + tableName + " columnname:" + columnName);
                                    return false;
                                }
                            }

                        }
                    }

                }

            }
        }

        return true;
    }

    private boolean compareColumnType(String oldColumnType, String columnType, SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        oldColumnType = oldColumnType.toUpperCase();
        columnType = columnType.toUpperCase();
        String oldColumnFlag = oldColumnType.substring(0, (oldColumnType.indexOf("(") != -1) ? oldColumnType.indexOf("(") : oldColumnType.length());
        String[] oldColumnLengthStrs = oldColumnType.replace(oldColumnFlag, "").replace("(", "").replace(")", "").split(",");
        String columnFlag = columnType.substring(0, (columnType.indexOf("(") != -1) ? columnType.indexOf("(") : columnType.length());
        String[] columnLengthStrs = columnType.replace(columnFlag, "").replace("(", "").replace(")", "").split(",");

        if (!oldColumnFlag.equals(columnFlag)) {
            sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本中涉及字段类型变更！");
            return false;
        }


        if (oldColumnLengthStrs != null && columnLengthStrs != null) {

            if (oldColumnLengthStrs.length != columnLengthStrs.length) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本字段类型涉及精度变更！");
                return false;
            }

            for (int i = 0; i < columnLengthStrs.length; i++) {
                Integer columnLength = Integer.parseInt("".equals(columnLengthStrs[i]) ? "-1" : columnLengthStrs[i]);
                Integer oldColumnLength = Integer.parseInt("".equals(oldColumnLengthStrs[i]) ? "-1" : oldColumnLengthStrs[i]);
                if (columnLength < oldColumnLength) {
                    sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本字段长度不允许改小！");
                    return false;
                }

            }
        }

        return true;

    }


    //检查一个对象的脚本，主要做合法性检查
    private boolean checkOneEntitySQL(SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        if (checkSQLScript(sinoSigSQLLogEntity)) {
            sinoSigSQLLogEntity.executestate = "3";
            return true;
        } else {
            sinoSigSQLLogEntity.executestate = "-1";
            return false;
        }

    }






    @Override
    public List<SinoSigSQLLogEntity> checkSQL(List<SinoSigSQLLogEntity> list) throws Exception {
        //分组+单个校验
        Map<String, List<SinoSigSQLLogEntity>> ddlMapList = new HashMap<>();
        Map<String, List<SinoSigSQLLogEntity>> dmlMapList = new HashMap<>();

        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            //初步校验失败的不进入分组校验
            if (checkOneEntitySQL(sinoSigSQLLogEntity) == false) {
                continue;
            }else {
                //分组
                if ("DDL".equals(sinoSigSQLLogEntity.sqltype.toUpperCase())) {
                    String batchNo = sinoSigSQLLogEntity.batchno;
                    List<SinoSigSQLLogEntity> sqlLogEntities = ddlMapList.get(batchNo);
                    if (sqlLogEntities == null) {
                        sqlLogEntities = new ArrayList<>();
                    }
                    sqlLogEntities.add(sinoSigSQLLogEntity);
                    ddlMapList.put(batchNo, sqlLogEntities);
                } else if("DML".equals(sinoSigSQLLogEntity.sqltype.toUpperCase())){
                    String batchNo = sinoSigSQLLogEntity.batchno;
                    List<SinoSigSQLLogEntity> sqlLogEntities = dmlMapList.get(batchNo);
                    if (sqlLogEntities == null) {
                        sqlLogEntities = new ArrayList<>();
                    }
                    sqlLogEntities.add(sinoSigSQLLogEntity);
                    dmlMapList.put(batchNo, sqlLogEntities);
                }
            }
        }


        //分组校验 只有ddl语句做分组校验
        Set<String> keySet = ddlMapList.keySet();
        for (String key : keySet) {
            List<SinoSigSQLLogEntity> sqlLogEntities = ddlMapList.get(key);
            VerificationCheck verificationCheck=new  VerificationCheck("test");
            verificationCheck.checkDDL(sqlLogEntities);
        }


        for(SinoSigSQLLogEntity sinoSigSQLLogEntity :list){
            sinoSigSQLLogService.updateStateSinoSingSQLLog(sinoSigSQLLogEntity);
        }

        return list;
    }
}
