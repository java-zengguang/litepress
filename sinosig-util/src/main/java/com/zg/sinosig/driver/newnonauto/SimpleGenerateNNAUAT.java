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
       list= execute(list);
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
        String empowermentTableStr = sinoSigSQLLogEntity.needpowertables;
        String databaseName = sinoSigSQLLogEntity.databasename;
        String owner="";

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
            String[] empowermentTables = empowermentTableStr.split("/");
            for (String empowermentTable : empowermentTables) {
                empowermentTable = empowermentTable.trim();
                String proEmpowermentSql = "";
                String devEmpowermentSql = "";

                if (true) {
                    String[] proPowerUsers = {"wushengrun-phq", "zengguang-phq", "tangjunxiang_ghq", "luweijun_ghq", "zhenglixue_phq"};
                    for (String powerUser : proPowerUsers) {
                        proEmpowermentSql = proEmpowermentSql + "grant select on " + databaseName + "." + empowermentTable + " to " + powerUser + " ;\n" +
                                "create or replace " + powerUser + "." + empowermentTable + " for " + databaseName + "." + empowermentTable + " ;\n";
                    }

                    String[] devPowerUser = {"nonvehread","nonvehwrite","nonveh"};
                    for (String powerUser : devPowerUser) {
                        devEmpowermentSql = devEmpowermentSql + "grant select on " + databaseName + "." + empowermentTable + " to " + powerUser + " ;\n" +
                                "create or replace " + powerUser + "." + empowermentTable + " for " + databaseName + "." + empowermentTable + " ;\n";
                    }

                    devEmpowermentSqlALL = devEmpowermentSqlALL + devEmpowermentSql;
                    proEmpowermentSqlALL = proEmpowermentSqlALL + proEmpowermentSql;
                }

            }
        }
        sinoSigSQLLogEntity.devsql = sinoSigSQLLogEntity.basesql + "\r\n" + devEmpowermentSqlALL;
        sinoSigSQLLogEntity.prosql = sinoSigSQLLogEntity.basesql + "\r\n" + proEmpowermentSqlALL;
        return sinoSigSQLLogEntity;
    }



}
