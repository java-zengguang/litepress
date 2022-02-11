package com.zg.sinosig.driver.newnonauto;

import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.generate.SimpleGenerate;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.ArrayList;
import java.util.List;

public class SimpleGenerateNNAUAT extends SimpleGenerate implements SunAutoDriver {


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if ("new-non-auto".equals(systemFlag)) {
            execute(list);
        }
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

        if (empowermentTableStr != null && !empowermentTableStr.equals("")) {
            String[] empowermentTables = empowermentTableStr.split("/");
            for (String empowermentTable : empowermentTables) {
                empowermentTable = empowermentTable.trim();
                String proEmpowermentSql = "";
                String devEmpowermentSql = "";
                if (true) {
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
        }
        sinoSigSQLLogEntity.devsql = sinoSigSQLLogEntity.basesql + "\r\n" + devEmpowermentSqlALL;
        sinoSigSQLLogEntity.prosql = sinoSigSQLLogEntity.basesql + "\r\n" + proEmpowermentSqlALL;
        return sinoSigSQLLogEntity;
    }


    public List<SinoSigSQLLogEntity> execute(List<SinoSigSQLLogEntity> list) {
        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            if (checkCustomModelRule(sinoSigSQLLogEntity)) {
                sinoSigSQLLogEntity=machiningSQL(sinoSigSQLLogEntity);
                sinoSigSQLLogEntity.environment="uat";
                sinoSigSQLLogEntity.executestate="3";
            }
        }
        return list;
    }

}
