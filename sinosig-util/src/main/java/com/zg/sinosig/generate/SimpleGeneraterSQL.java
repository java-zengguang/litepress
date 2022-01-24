package com.zg.sinosig.generate;

import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.util.io.POIUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class SimpleGeneraterSQL implements GenerateSQL {


    private File file;
    private String executeBatchNo;
    private String environment;

   // private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyClass(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog");

    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(),"insertSinoSingSQLLog,updateStateSinoSingSQLLog"));

    public SimpleGeneraterSQL(File file, String executeBatchNo, String environment) {
        this.file = file;
        this.executeBatchNo = executeBatchNo;
        this.environment = environment;
    }

    //以入口为模板复制加工处新的对象
    private SinoSigSQLLogEntity machiningSQL(SinoSigSQLLogEntity sourceSQLEntity) {
        SinoSigSQLLogEntity sinoSigSQLLogEntity = (SinoSigSQLLogEntity) sourceSQLEntity.clone();

        String proEmpowermentSqlALL = "";
        String devEmpowermentSqlALL = "";
        String empowermentTableStr = sinoSigSQLLogEntity.needpowertables;

        if (empowermentTableStr != null && !empowermentTableStr.equals("")) {
            String[] empowermentTables = empowermentTableStr.split("/");
            for (String empowermentTable : empowermentTables) {
                empowermentTable = empowermentTable.trim();
                String proEmpowermentSql = "";
                String devEmpowermentSql = "";
                if ("保单库".equals(sinoSigSQLLogEntity.databasename)) {
                    proEmpowermentSql = "grant select on nvpolicy.table_name to wushengrun_phq;\n" +
                            "create synonym wushengrun_phq.table_name for nvpolicy.table_name;\n" +
                            "grant select,insert,update,delete on nvpolicy.table_name to nonveh;\n" +
                            "create synonym nonveh.table_name for nvpolicy.table_name;\n" +
                            "grant select on nvpolicy.table_name to tangjunxiang_ghq;\n" +
                            "create synonym tangjunxiang_ghq.table_name for nvpolicy.table_name;\n" +
                            "grant select on nvpolicy.table_name to luweijun_ghq;\n" +
                            "create synonym luweijun_ghq.table_name for nvpolicy.table_name;";

                    devEmpowermentSql = "grant select on nvpolicy.table_name to nonvehread;\n" +
                            "grant select,insert,update,delete on nvpolicy.table_name to nonvehwrite;\n" +
                            "create or replace synonym nonvehread.table_name for nvpolicy.table_name;\n" +
                            "create or replace synonym nonvehwrite.table_name for nvpolicy.table_name;";

                }
                if ("产品工厂库".equals(sinoSigSQLLogEntity.databasename)) {
                    proEmpowermentSql = "grant select on nvfactory.table_name to wushengrun_phq;\n" +
                            "create synonym wushengrun_phq.table_name for nvfactory.table_name;\n" +
                            "grant select,insert,update,delete on nvfactory.table_name to nonveh;\n" +
                            "create synonym nonveh.table_name for nvfactory.table_name;\n" +
                            "grant select on nvfactory.table_name to tangjunxiang_ghq;\n" +
                            "create synonym tangjunxiang_ghq.table_name for nvfactory.table_name;\n" +
                            "grant select on nvfactory.table_name to luweijun_ghq;\n" +
                            "create synonym luweijun_ghq.table_name for nvfactory.table_name;";

                    devEmpowermentSql = "grant select on nvfactory.table_name to nonvehread;\n" +
                            "grant select,insert,update,delete on nvfactory.table_name to nonvehwrite;\n" +
                            "create or replace synonym nonvehread.table_name for nvfactory.table_name;\n" +
                            "create or replace synonym nonvehwrite.table_name for nvfactory.table_name;";
                }
                if ("投保单库".equals(sinoSigSQLLogEntity.databasename)) {
                    proEmpowermentSql = "grant select on nvproposal.table_name to wushengrun_phq;\n" +
                            "create synonym wushengrun_phq.table_name for nvproposal.table_name;\n" +
                            "grant select,insert,update,delete on nvproposal.table_name to nonveh;\n" +
                            "create synonym nonveh.table_name for nvproposal.table_name;\n" +
                            "grant select on nvproposal.table_name to wangzhenfeng_wb;\n" +
                            "create synonym wangzhenfeng_wb.table_name for nvproposal.table_name;\n" +
                            "grant select on nvproposal.table_name to luweijun_ghq;\n" +
                            "create synonym luweijun_ghq.table_name for nvproposal.table_name;";

                    devEmpowermentSql = "grant select on nvproposal.table_name to nonvehread;\n" +
                            "grant select,insert,update,delete on nvproposal.table_name to nonvehwrite;\n" +
                            "create or replace synonym nonvehread.table_name for nvproposal.table_name;\n" +
                            "create or replace synonym nonvehwrite.table_name for nvproposal.table_name;";
                }
                if ("批单修改库".equals(sinoSigSQLLogEntity.databasename)) {
                    proEmpowermentSql = "grant select on nvendorsement.table_name to wushengrun_phq;\n" +
                            "create synonym wushengrun_phq.table_name for nvendorsement.table_name;\n" +
                            "grant select,insert,update,delete on nvendorsement.table_name to nonveh;\n" +
                            "create synonym nonveh.table_name for nvendorsement.table_name;";

                    devEmpowermentSql = "grant select on nvendorsement.table_name to nonvehread;\n" +
                            "grant select,insert,update,delete on nvendorsement.table_name to nonvehwrite;\n" +
                            "create or replace synonym nonvehread.table_name for nvendorsement.table_name;\n" +
                            "create or replace synonym nonvehwrite.table_name for nvendorsement.table_name;";
                }
                if ("统一工作台库".equals(sinoSigSQLLogEntity.databasename)) {
                    proEmpowermentSql = "grant select on nvportal.table_name to wushengrun_phq;\n" +
                            "create synonym wushengrun_phq.table_name for nvportal.table_name;\n" +
                            "grant select,insert,update,delete on nvportal.table_name to nonveh;\n" +
                            "create synonym nonveh.table_name for nvportal.table_name;\n" +
                            "grant select on nvportal.table_name to tangjunxiang_ghq;\n" +
                            "create synonym tangjunxiang_ghq.table_name for nvportal.table_name;\n" +
                            "grant select on nvportal.table_name to luweijun_ghq;\n" +
                            "create synonym luweijun_ghq.table_name for nvportal.table_name;";

                    devEmpowermentSql = "grant select on nvportal.table_name to nonvehread;\n" +
                            "grant select,insert,update,delete on nvportal.table_name to nonvehwrite;\n" +
                            "create or replace synonym nonvehread.table_name for nvportal.table_name;\n" +
                            "create or replace synonym nonvehwrite.table_name for nvportal.table_name;";
                }

                if ("汇总库".equals(sinoSigSQLLogEntity.databasename)) {


                    devEmpowermentSql = "grant select on nvpolicy.table_name to nonvehread;\n" +
                            "grantselect,insert,update,delete on nvpolicy.table_name to nonvehwrite;\n" +
                            "create or replace synonym nonvehread.table_name for nvpolicy.table_name;\n" +
                            "create or replace synonym nonvehwrite.table_name for nvpolicy.table_name;";
                }


                if (!"".equals(proEmpowermentSql)) {
                    proEmpowermentSql = "\r\n" + proEmpowermentSql.replaceAll("table_name", empowermentTable) + "\r\n";
                }


                if (!"".equals(devEmpowermentSql)) {
                    devEmpowermentSql = "\r\n" + devEmpowermentSql.replaceAll("table_name", empowermentTable) + "\r\n";
                }

                devEmpowermentSqlALL = devEmpowermentSqlALL + devEmpowermentSql;
                proEmpowermentSqlALL = proEmpowermentSqlALL + proEmpowermentSql;
            }

        }
        sinoSigSQLLogEntity.devsql = sinoSigSQLLogEntity.basesql + "\r\n" + devEmpowermentSqlALL;
        sinoSigSQLLogEntity.prosql = sinoSigSQLLogEntity.basesql + "\r\n" + proEmpowermentSqlALL;
        return sinoSigSQLLogEntity;
    }



    public List<SinoSigSQLLogEntity> generateSQLFromExcel(File file) throws IOException, SQLException, IllegalAccessException {
        List<SinoSigSQLLogEntity> logEntities = new ArrayList<>();

        if (file.exists()) {
            if (file.isDirectory()) {

                File[] files = file.listFiles();
                for (File f : files) {
                    logEntities.addAll(generateSQLFromExcel(f));
                }
            } else if (file.getName().endsWith(".xls")) {
                InputStream inputStream = new FileInputStream(file);
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
                List<Map> mapList = POIUtils.readExcel(hssfWorkbook, "Sheet1");
                Random random = new Random();
                String batchNo = "JB" + new Date().getTime() + random.nextInt();

                for (int i = 1; i < mapList.size(); i++) {
                    Map<String, String> map = mapList.get(i);
                    if (map.get("序号") != null && !"".equals(map.get("序号").trim())) {

                        SinoSigSQLLogEntity baseEntity = new SinoSigSQLLogEntity();
                        baseEntity.batchno = batchNo;
                        baseEntity.serialno = map.get("序号").trim();

                        if (map.get("数据库") != null) {
                            baseEntity.databasename = map.get("数据库").trim();
                        }
                        if (map.get("提交人") != null) {
                            baseEntity.applyusername = map.get("提交人").trim();
                        }
                        if (map.get("发布计划名称") != null) {
                            baseEntity.planname = map.get("发布计划名称").trim();
                        }

                        if (map.get("需求ID") != null) {
                            baseEntity.demandid = map.get("需求ID").trim();
                        }

                        if (map.get("需求名称") != null) {
                            baseEntity.demandname = map.get("需求名称").trim();
                        }

                        if (map.get("脚本类型") != null) {
                            baseEntity.sqltype = map.get("脚本类型").trim();
                        }
                        if (map.get("是否为开关配置类脚本") != null) {
                            baseEntity.isconfig = map.get("是否为开关配置类脚本").trim();

                        }
                        if (map.get("需赋权表名") != null) {
                            baseEntity.needpowertables = map.get("需赋权表名").trim();
                            if (baseEntity.needpowertables != null && !"".equals(baseEntity.needpowertables)) {
                                baseEntity.needPowerTableArray = baseEntity.needpowertables.split(",");
                            }
                        }
                        if (map.get("脚本") != null) {
                            baseEntity.basesql = map.get("脚本").trim();

                        }
                        if (map.get("备注") != null) {
                            baseEntity.sqldescribe = map.get("备注").trim();
                        }
                        if (map.get("脚本用途") != null) {
                            baseEntity.sqlpurpose = map.get("脚本用途").trim();
                        }

                        if(true){
                            if(!"保单库".equals(baseEntity.databasename)&&!"批单修改库".equals(baseEntity.databasename)&&!"投保单库".equals(baseEntity.databasename)){
                                baseEntity.setErrormassage("数据库名无效!");
                                baseEntity.executestate="-1";
                            }
                            if(!"是".equals(baseEntity.isconfig)&&!"否".equals(baseEntity.isconfig)){
                                baseEntity.setErrormassage("开关配置填入错误!");
                                baseEntity.executestate="-1";
                            }
                            if(!"DDL".equals(baseEntity.sqltype)&&!"DML".equals(baseEntity.sqltype)){
                                baseEntity.setErrormassage("脚本类型填入错误!");
                                baseEntity.executestate="-1";
                            }
                            if(!"建表".equals(baseEntity.sqlpurpose)&&!"拉长字段".equals(baseEntity.sqlpurpose)&&!"增加字段".equals(baseEntity.sqlpurpose)&&!"数据操作".equals(baseEntity.sqlpurpose)){
                                baseEntity.setErrormassage("脚本用途填入错误!");
                                baseEntity.executestate="-1";
                            }
                            if("DML".equals(baseEntity.sqltype)&&!"数据操作".equals(baseEntity.sqlpurpose)){
                                baseEntity.setErrormassage("脚本用途填入错误!");
                                baseEntity.executestate="-1";
                            }
                            if("DDL".equals(baseEntity.sqltype)&&!"建表拉长字段增加字段".contains(baseEntity.sqlpurpose)){
                                baseEntity.setErrormassage("脚本用途填入错误!");
                                baseEntity.executestate="-1";
                            }
                            if("建表".equals(baseEntity.sqlpurpose)&&!baseEntity.basesql.contains("create")){
                                baseEntity.setErrormassage("脚本用途填入错误!");
                                baseEntity.executestate="-1";
                            }
                            if("建表".equals(baseEntity.sqlpurpose)&&baseEntity.basesql.contains("create")){
                                String[] array=baseEntity.needPowerTableArray;
                                if(array==null||array.length==0){
                                    baseEntity.setErrormassage("建表请赋权！");
                                    baseEntity.executestate="-1";
                                }else {

                                    for(String tableName:array){
                                        if(!baseEntity.basesql.contains(tableName)){
                                            baseEntity.setErrormassage("建表缺少赋权!");
                                            baseEntity.executestate="-1";
                                        }
                                    }

                                }

                            }

                        }
                        //生成uat模型脚本
                        baseEntity.environment = environment;
                        baseEntity.executestate = "0";
                        baseEntity.executebatchno = executeBatchNo;
                        SinoSigSQLLogEntity sinoSigSQLLogEntity = machiningSQL(baseEntity);  //以入口为模板复制加工处新的对
                        logEntities.add(sinoSigSQLLogEntity);
                        sinoSigSQLLogService.insertSinoSingSQLLog(sinoSigSQLLogEntity);
                    }

                }
            }
        }
        return logEntities;
    }

    @Override
    public List initLoadSinoSingSQL() {
        List<SinoSigSQLLogEntity> list=new ArrayList();
        try {
            list=generateSQLFromExcel(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }

}
