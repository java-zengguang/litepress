package com.zg.webdemo.util.sinosing;


import com.mchange.v2.sql.SqlUtils;
import com.zg.bean.entity.OptionDB;
import com.zg.handler.ProxyUtils;
import com.zg.util.io.POIUtils;
import com.zg.util.sinosing.DatabaseUtil;
import com.zg.util.sinosing.JDBCUtil;
import com.zg.webdemo.entity.SinoSingSQLLogEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.python.antlr.ast.Str;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class NewExecuteSql {

    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyClass(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog");
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private File excelFile;
    private List<SinoSingSQLLogEntity> logEntities = new ArrayList<>();//excel映射

    public NewExecuteSql(File excelFile) {
        this.excelFile = excelFile;
    }

    private void loadExcel() throws IOException {
        InputStream inputStream = new FileInputStream(excelFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        List<Map> mapList = POIUtils.readExcel(hssfWorkbook, "Sheet1");
        Random random = new Random();
        String batchNo = "JB" + new Date().getTime() + random.nextInt();

        for (int i = 1; i < mapList.size(); i++) {
            Map<String, String> map = mapList.get(i);
            if (map.get("序号") != null && !"".equals(map.get("序号").trim())) {

                SinoSingSQLLogEntity sinoSingSQLLogEntity = new SinoSingSQLLogEntity();
                sinoSingSQLLogEntity.batchno = batchNo;
                sinoSingSQLLogEntity.serialno = map.get("序号").trim();

                if (map.get("数据库") != null) {
                    sinoSingSQLLogEntity.databasename = map.get("数据库").trim();
                }
                if (map.get("提交人") != null) {
                    sinoSingSQLLogEntity.applyusername = map.get("提交人").trim();
                }
                if (map.get("发布计划名称") != null) {
                    sinoSingSQLLogEntity.planname = map.get("发布计划名称").trim();
                }

                if (map.get("需求ID") != null) {
                    sinoSingSQLLogEntity.demandid = map.get("需求ID").trim();
                }

                if (map.get("需求名称") != null) {
                    sinoSingSQLLogEntity.demandname = map.get("需求名称").trim();
                }

                if (map.get("脚本类型") != null) {
                    sinoSingSQLLogEntity.sqltype = map.get("脚本类型").trim();
                }
                if (map.get("是否为开关配置类脚本") != null) {
                    sinoSingSQLLogEntity.isconfig = map.get("是否为开关配置类脚本").trim();
                }
                if (map.get("需赋权表名") != null) {
                    sinoSingSQLLogEntity.needpowertables = map.get("需赋权表名").trim();
                    if (sinoSingSQLLogEntity.needpowertables != null && !"".equals(sinoSingSQLLogEntity.needpowertables)) {
                        sinoSingSQLLogEntity.needPowerTableArray = sinoSingSQLLogEntity.needpowertables.split(",");
                    }
                }
                if (map.get("脚本") != null) {
                    sinoSingSQLLogEntity.basesql = map.get("脚本").trim();

                }
                if (map.get("备注") != null) {
                    sinoSingSQLLogEntity.sqldescribe = map.get("备注").trim();
                }
                if (map.get("脚本用途") != null) {
                    sinoSingSQLLogEntity.sqlpurpose = map.get("脚本用途").trim();
                }
                logEntities.add(sinoSingSQLLogEntity);
            }
        }

    }

    private void sqlBatch() throws IOException {

        for (SinoSingSQLLogEntity sourceSQLLogEntity : logEntities) {


            if (!checkSQL(sourceSQLLogEntity.basesql)) {
                continue;
            }

            String typStr = sourceSQLLogEntity.environment;
            if (typStr == null || "".equals(typStr)) {
                typStr = "uat/stage";
            }
            typStr = typStr.trim().toLowerCase();
            String types[] = typStr.split("/");
            int flag = 0;
            for (String type : types) {
                if (flag < 0) {
                    logger.info("上一个环境执行错误，不予执行！");
                    continue;
                }
                sourceSQLLogEntity.environment = type;
                OptionDB optionDB = DatabaseUtil.getOptionDB(type, sourceSQLLogEntity.databasename);
                SinoSingSQLLogEntity sinoSingSQLLogEntity = machiningSQL(sourceSQLLogEntity);  //以入口为模板复制加工处新的对象
                try {
                    sinoSingSQLLogEntity = sinoSigSQLLogService.insertSinoSingSQLLog(sinoSingSQLLogEntity);
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                    logger.info("数据库插入错误！");
                    continue;
                }
                if (execSql(optionDB, sinoSingSQLLogEntity)) {
                    saveSqlFile(sinoSingSQLLogEntity);
                    System.out.println(sinoSingSQLLogEntity.serialno + "已生成生产脚本");
                    flag = 1;

                } else {
                    System.out.println(sinoSingSQLLogEntity.serialno + "执行出错，不生成脚本");
                    flag = -1;
                }
                try {
                    sinoSingSQLLogEntity.executestate = flag + "";
                    sinoSigSQLLogService.updateStateSinoSingSQLLog(sinoSingSQLLogEntity);
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }

            }

        }


    }


    //以入口为模板复制加工处新的对象
    private SinoSingSQLLogEntity machiningSQL(SinoSingSQLLogEntity sourceSQLEntity) {
        SinoSingSQLLogEntity sinoSingSQLLogEntity = (SinoSingSQLLogEntity) sourceSQLEntity.clone();

        String proEmpowermentSqlALL = "";
        String devEmpowermentSqlALL = "";
        String empowermentTableStr = sinoSingSQLLogEntity.needpowertables;

        if (empowermentTableStr != null && !empowermentTableStr.equals("")) {
            String[] empowermentTables = empowermentTableStr.split("/");
            for (String empowermentTable : empowermentTables) {
                empowermentTable = empowermentTable.trim();
                String proEmpowermentSql = "";
                String devEmpowermentSql = "";
                if ("保单库".equals(sinoSingSQLLogEntity.databasename)) {
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
                            "create synonym nonvehread.table_name for nvpolicy.table_name;\n" +
                            "create synonym nonvehwrite.table_name for nvpolicy.table_name;";

                }
                if ("产品工厂库".equals(sinoSingSQLLogEntity.databasename)) {
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
                            "create synonym nonvehread.table_name for nvfactory.table_name;\n" +
                            "create synonym nonvehwrite.table_name for nvfactory.table_name;";
                }
                if ("投保单库".equals(sinoSingSQLLogEntity.databasename)) {
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
                            "create synonym nonvehread.table_name for nvproposal.table_name;\n" +
                            "create synonym nonvehwrite.table_name for nvproposal.table_name;";
                }
                if ("批单修改库".equals(sinoSingSQLLogEntity.databasename)) {
                    proEmpowermentSql = "grant select on nvendorsement.table_name to wushengrun_phq;\n" +
                            "create synonym wushengrun_phq.table_name for nvendorsement.table_name;\n" +
                            "grant select,insert,update,delete on nvendorsement.table_name to nonveh;\n" +
                            "create synonym nonveh.table_name for nvendorsement.table_name;";

                    devEmpowermentSql = "grant select on nvendorsement.table_name to nonvehread;\n" +
                            "grant select,insert,update,delete on nvendorsement.table_name to nonvehwrite;\n" +
                            "create synonym nonvehread.table_name for nvendorsement.table_name;\n" +
                            "create synonym nonvehwrite.table_name for nvendorsement.table_name;";
                }
                if ("统一工作台库".equals(sinoSingSQLLogEntity.databasename)) {
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
                            "create synonym nonvehread.table_name for nvportal.table_name;\n" +
                            "create synonym nonvehwrite.table_name for nvportal.table_name;";
                }

                if ("汇总库".equals(sinoSingSQLLogEntity.databasename)) {


                    devEmpowermentSql = "grant select on nvpolicy.table_name to nonvehread;\n" +
                            "grant select,insert,update,delete on nvpolicy.table_name to nonvehwrite;\n" +
                            "create synonym nonvehread.table_name for nvpolicy.table_name;\n" +
                            "create synonym nonvehwrite.table_name for nvpolicy.table_name;";
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
        sinoSingSQLLogEntity.devsql = sinoSingSQLLogEntity.basesql + "\r\n" + devEmpowermentSqlALL;
        sinoSingSQLLogEntity.prosql = sinoSingSQLLogEntity.basesql + "\r\n" + proEmpowermentSqlALL;
        return sinoSingSQLLogEntity;
    }

    private boolean checkSQL(String vsql) {

        SQLUtils.checkSQLScript(vsql);

        return true;
    }


    private boolean execSql(OptionDB optionDB, SinoSingSQLLogEntity singSQLLogEntity) throws IOException {

        ArrayList sqlList = new ArrayList();
        String sql = singSQLLogEntity.devsql;

        if (sql.contains(";")) {
            String str[] = sql.split(";");
            for (String s : str) {
                s = s.trim();
                if (s != null && !"".equals(s)) {
                    sqlList.add(s);
                }
            }
        } else {
            sqlList.add(sql);
        }

        if (!SQLUtils.checkSQLScript(sql) || !SQLUtils.checkSQLList(sqlList)) {
            logger.info("脚本校验未通过");
            singSQLLogEntity.errormassage ="脚本校验未通过";
            return false;
        }


        JDBCUtil jdbcUtil = new JDBCUtil(optionDB);
        if (jdbcUtil == null) {
            singSQLLogEntity.errormassage = "链接数据库失败！";
            System.out.println("链接数据库失败！");
            return false;
        }
        try {
            System.out.println("------" + optionDB.getUrl() + "---" + optionDB.getUsername());
            jdbcUtil.batchSql(sqlList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            singSQLLogEntity.errormassage = throwables.getMessage();
            return false;
        } finally {
            jdbcUtil.commit();
        }
        return true;

    }

    private boolean saveSqlFile(SinoSingSQLLogEntity sinoSingSQLLogEntity) throws IOException {
        String planType = "";
        if (sinoSingSQLLogEntity.planname.contains("灰度")) {
            planType = "灰度";
        }
        String fileName = "";
        fileName = fileName + sinoSingSQLLogEntity.serialno.replace(".0", "") + "-";
        fileName = fileName + sinoSingSQLLogEntity.sqltype.trim().toUpperCase() + "_";
        fileName = fileName + sinoSingSQLLogEntity.planname + "-";
        fileName = fileName + planType + "-";
        fileName = fileName + sinoSingSQLLogEntity.databasename + "-";
        fileName = fileName + sinoSingSQLLogEntity.applyusername + "-";
        fileName = fileName + sinoSingSQLLogEntity.demandname + ".sql";
        String content = sinoSingSQLLogEntity.prosql;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        String path = "D:\\test\\temp\\" + year + "年\\" + sinoSingSQLLogEntity.planname;

        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdir();
        }

        if ("是".equals(sinoSingSQLLogEntity.isconfig.trim())) {
            path = path + "\\开关管理";
        } else {
            path = path + "\\脚本管理";
        }

        pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdir();
        }

        File file = new File(path, fileName);

        if (file.exists()) {
            file.createNewFile();
        }
        Writer writer = new FileWriter(file);
        writer.write(content);
        writer.flush();
        writer.close();
        return true;
    }


    public void domain() throws IOException {
        loadExcel();
        sqlBatch();
    }

    public static void main(String args[]) throws IOException {

        //  sinoSingUtil.sqlBatch("D:\\test\\20211021\\管金龙2.xls");
        File dirFile = new File("D:\\test\\记录\\temp");

        File[] files = dirFile.listFiles();
        for (File file : files) {
            NewExecuteSql sinoSingUtil = new NewExecuteSql(file);
            sinoSingUtil.domain();
        }




    }

}
