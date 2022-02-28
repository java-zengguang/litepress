package com.zg.sinosig.driver.newnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.generate.SimpleGenerate;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;

import java.util.List;

public class SimpleGenerateNNAUAT extends SimpleGenerate implements SunAutoDriver {


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if ("new-non-auto".equals(systemFlag)) {
            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                if (checkCustomModelRule(sinoSigSQLLogEntity)) {
                    sinoSigSQLLogEntity = machiningSQL(sinoSigSQLLogEntity);
                    sinoSigSQLLogEntity.environment = "uat";
                    sinoSigSQLLogEntity.executestate = "3";
                }
            }
        }
        list = execute(list);
        return list;
    }


    boolean checkCustomModelRule(SinoSigSQLLogEntity baseEntity) {

        if ("new-non-auto".endsWith(baseEntity.systemflag)) {
            if (!"保单库".equals(baseEntity.databasename) && !"批单修改库".equals(baseEntity.databasename) && !"投保单库".equals(baseEntity.databasename)) {
                baseEntity.setErrormassage("数据库名无效!");
                baseEntity.executestate = "-1";
            }

            if (!"DDL".equals(baseEntity.sqltype) && !"DML".equals(baseEntity.sqltype)) {
                baseEntity.setErrormassage("脚本类型填入错误!");
                baseEntity.executestate = "-1";
            }
            if (!"建表".equals(baseEntity.sqlpurpose) && !"拉长字段".equals(baseEntity.sqlpurpose) && !"增加字段".equals(baseEntity.sqlpurpose) && !"数据操作".equals(baseEntity.sqlpurpose)) {
                baseEntity.setErrormassage("脚本用途填入错误!");
                baseEntity.executestate = "-1";
            }
            if ("DML".equals(baseEntity.sqltype) && !"数据操作".equals(baseEntity.sqlpurpose)) {
                baseEntity.setErrormassage("脚本用途填入错误!");
                baseEntity.executestate = "-1";
            }
            if ("DDL".equals(baseEntity.sqltype) && !"建表拉长字段增加字段".contains(baseEntity.sqlpurpose)) {
                baseEntity.setErrormassage("脚本用途填入错误!");
                baseEntity.executestate = "-1";
            }
            if ("建表".equals(baseEntity.sqlpurpose) && !baseEntity.basesql.contains("create")) {
                baseEntity.setErrormassage("脚本用途填入错误!");
                baseEntity.executestate = "-1";
            }
            if ("建表".equals(baseEntity.sqlpurpose) && baseEntity.basesql.contains("create")) {
                String[] array = baseEntity.needPowerTableArray;
                if (array == null || array.length == 0) {
                    baseEntity.setErrormassage("建表请赋权！");
                    baseEntity.executestate = "-1";
                } else {

                    for (String tableName : array) {
                        if (!baseEntity.basesql.contains(tableName)) {
                            baseEntity.setErrormassage("建表缺少赋权!");
                            baseEntity.executestate = "-1";
                        }
                    }
                }
            }
        }
        return true;
    }

    //以入口为模板复制加工处新的对象
    private SinoSigSQLLogEntity machiningSQL(SinoSigSQLLogEntity sinoSigSQLLogEntity) {

        String proEmpowermentSqlALL = "";
        String devEmpowermentSqlALL = "";
        String proSynSqlALL = "";
        String devSynSqlALL = "";
        String empowermentTableStr = sinoSigSQLLogEntity.needpowertables;
        String databaseName = sinoSigSQLLogEntity.databasename;
        String owner = "";

        switch (databaseName) {
            case "保单库": {
                owner = "nvpolicy";
                break;
            }
            case "投保单库": {
                owner = "nvproposal";
                break;
            }
            case "批单修改库": {
                owner = "nvendorsement";
                break;
            }
        }
        sinoSigSQLLogEntity.owner = owner;  //获取属主
        if (empowermentTableStr != null && !empowermentTableStr.equals("")) {
            String[] empowermentTables = sinoSigSQLLogEntity.needPowerTableArray;
            for (String empowermentTable : empowermentTables) {
                empowermentTable = empowermentTable.trim();
                String proEmpowermentSql = "";
                String proSynSql = "";
                String devEmpowermentSql = "";
                String devSynSql = "";

                if (true) {
                    String[] proPowerUsers = {"nonveh", "wushengrun_phq", "zengguang_phq", "tangjunxiang_ghq", "luweijun_ghq", "zhenglixue_phq", "liangyaoze_phq"};
                    for (String powerUser : proPowerUsers) {
                        if ("nonvehwrite".equals(powerUser)||"nonveh".equals(powerUser) ) {
                            proEmpowermentSql = proEmpowermentSql + "grant select,insert,update,delete  on " + owner + "." + empowermentTable + " to " + powerUser + " ;\n" ;
                            proSynSql=proSynSql+    "create or replace  synonym " + powerUser + "." + empowermentTable + " for " + owner + "." + empowermentTable + " ;\n";
                        }else {
                            proEmpowermentSql = proEmpowermentSql + "grant select on " + owner + "." + empowermentTable + " to " + powerUser + " ;\n" ;
                            proSynSql=proSynSql+    "create or replace  synonym " + powerUser + "." + empowermentTable + " for " + owner + "." + empowermentTable + " ;\n";
                        }
                    }

                    String[] devPowerUser = {"nonvehread", "nonvehwrite", "nonveh"};
                    for (String powerUser : devPowerUser) {
                        if ("nonvehwrite".equals(powerUser)) {
                            devEmpowermentSql = devEmpowermentSql + "grant  select,insert,update,delete on " + owner + "." + empowermentTable + " to " + powerUser + " ;\n" ;
                            devSynSql=devSynSql+     "create or replace  synonym " + powerUser + "." + empowermentTable + " for " + owner + "." + empowermentTable + " ;\n";

                        } else {
                            devEmpowermentSql = devEmpowermentSql + "grant select on " + owner + "." + empowermentTable + " to " + powerUser + " ;\n" ;
                            devSynSql=devSynSql+   "create or replace  synonym " + powerUser + "." + empowermentTable + " for " + owner + "." + empowermentTable + " ;\n";
                        }
                    }

                    devEmpowermentSqlALL = devEmpowermentSqlALL + devEmpowermentSql;
                    proEmpowermentSqlALL = proEmpowermentSqlALL + proEmpowermentSql;
                    devSynSqlALL = devSynSqlALL + devSynSql;
                    proSynSqlALL = proSynSqlALL + proSynSql;
                }

            }
        }
        sinoSigSQLLogEntity.devsql = sinoSigSQLLogEntity.basesql + "\r\n" + devEmpowermentSqlALL+ "\r\n" + devSynSqlALL;
        sinoSigSQLLogEntity.prosql = sinoSigSQLLogEntity.basesql + "\r\n" + proEmpowermentSqlALL + "\r\n" + proSynSqlALL;
        return sinoSigSQLLogEntity;
    }


}
