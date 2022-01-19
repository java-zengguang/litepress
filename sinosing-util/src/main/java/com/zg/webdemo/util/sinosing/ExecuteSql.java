package com.zg.webdemo.util.sinosing;


import com.zg.bean.entity.OptionDB;
import com.zg.sinosing.check.SQLUtils;
import com.zg.util.io.POIUtils;
import com.zg.util.sinosing.DatabaseUtil;
import com.zg.util.sinosing.JDBCUtil;
import com.zg.webdemo.entity.SinoSingSQLLogEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExecuteSql {


    public void sqlBatch(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        List<Map> mapList = POIUtils.readExcel(hssfWorkbook, "Sheet1");


        String batchNo = "JB" + new Date().getTime();
        List<SinoSingSQLLogEntity> logEntities=new ArrayList<>();
        for (int i = 1; i < mapList.size(); i++) {
            Map<String, String> map = mapList.get(i);

            String database = map.get("数据库");
            String sql = map.get("脚本");
            String num = map.get("序号");
            //去掉空行
            if(num==null || "".equals(num)){
                mapList.remove(i);
                continue;
            }

            String empowermentTableStr = map.get("需赋权表名");
            String typStr = map.get("执行环境");
            if (typStr == null || "".equals(typStr)) {
                typStr = "int/uat/stage";
              //  typStr = "stage";
            }
            typStr=typStr.trim().toLowerCase();
            String types[] = typStr.split("/");
            for (String type : types) {
                OptionDB optionDB ;
      /*          if("是".equals(map.get("是否为开关配置类脚本").trim())){
                    optionDB =DatabaseUtil.getConfigOptionDB();
                }else{
                    optionDB =DatabaseUtil.getOptionDB(type, database);
                }*/

                optionDB = DatabaseUtil.getOptionDB(type, database);

                if (empowermentTableStr != null && !empowermentTableStr.equals("")) {
                    String proEmpowermentSqlALL = "";
                    String devEmpowermentSqlALL = "";
                    String[] empowermentTables = empowermentTableStr.split("/");
                    for (String empowermentTable : empowermentTables) {
                        empowermentTable = empowermentTable.trim();
                        String proEmpowermentSql = "";
                        String devEmpowermentSql = "";
                        if ("保单库".equals(database)) {
                            proEmpowermentSql = "grant select on nvpolicy.table_name to wushengrun_phq;\n" +
                                    "create synonym wushengrun_phq.table_name for nvpolicy.table_name;\n" +
                                    "grant select,insert,update,delete on nvpolicy.table_name to nonveh;\n" +
                                    "create synonym nonveh.table_name for nvpolicy.table_name;\n" +
                                    "grant select on nvpolicy.table_name to tangjunxiang_ghq;\n" +
                                    "create synonym tangjunxiang_ghq.table_name for nvpolicy.table_name;\n" +
                                    "grant select on nvpolicy.table_name to luweijun_ghq;\n" +
                                    "create synonym luweijun_ghq.table_name for nvpolicy.table_name;";

                            devEmpowermentSql = "grant select on nvpolicy.table_name to nonvehread;\n" +
                                    "create synonym nonvehread.table_name for nvpolicy.table_name;\n" +
                                    "grant select,insert,update,delete on nvpolicy.table_name to nonvehwrite;\n" +
                                    "create synonym nonvehwrite.table_name for nvpolicy.table_name;";

                        }
                        if ("产品工厂库".equals(database)) {
                            proEmpowermentSql = "grant select on nvfactory.table_name to wushengrun_phq;\n" +
                                    "create synonym wushengrun_phq.table_name for nvfactory.table_name;\n" +
                                    "grant select,insert,update,delete on nvfactory.table_name to nonveh;\n" +
                                    "create synonym nonveh.table_name for nvfactory.table_name;\n" +
                                    "grant select on nvfactory.table_name to tangjunxiang_ghq;\n" +
                                    "create synonym tangjunxiang_ghq.table_name for nvfactory.table_name;\n" +
                                    "grant select on nvfactory.table_name to luweijun_ghq;\n" +
                                    "create synonym luweijun_ghq.table_name for nvfactory.table_name;";

                            devEmpowermentSql = "grant select on nvfactory.table_name to nonvehread;\n" +
                                    "create synonym nonvehread.table_name for nvfactory.table_name;\n" +
                                    "grant select,insert,update,delete on nvfactory.table_name to nonvehwrite;\n" +
                                    "create synonym nonvehwrite.table_name for nvfactory.table_name;";
                        }
                        if ("投保单库".equals(database)) {
                            proEmpowermentSql = "grant select on nvproposal.table_name to wushengrun_phq;\n" +
                                    "create synonym wushengrun_phq.table_name for nvproposal.table_name;\n" +
                                    "grant select,insert,update,delete on nvproposal.table_name to nonveh;\n" +
                                    "create synonym nonveh.table_name for nvproposal.table_name;\n" +
                                    "grant select on nvproposal.table_name to wangzhenfeng_wb;\n" +
                                    "create synonym wangzhenfeng_wb.table_name for nvproposal.table_name;\n" +
                                    "grant select on nvproposal.table_name to luweijun_ghq;\n" +
                                    "create synonym luweijun_ghq.table_name for nvproposal.table_name;";

                            devEmpowermentSql = "grant select on nvproposal.table_name to nonvehread;\n" +
                                    "create synonym nonvehread.table_name for nvproposal.table_name;\n" +
                                    "grant select,insert,update,delete on nvproposal.table_name to nonvehwrite;\n" +
                                    "create synonym nonvehwrite.table_name for nvproposal.table_name;";
                        }
                        if ("批单修改库".equals(database)) {
                            proEmpowermentSql = "grant select on nvendorsement.table_name to wushengrun_phq;\n" +
                                    "create synonym wushengrun_phq.table_name for nvendorsement.table_name;\n" +
                                    "grant select,insert,update,delete on nvendorsement.table_name to nonveh;\n" +
                                    "create synonym nonveh.table_name for nvendorsement.table_name;";

                            devEmpowermentSql = "grant select on nvendorsement.table_name to nonvehread;\n" +
                                    "create synonym nonvehread.table_name for nvendorsement.table_name;\n" +
                                    "grant select,insert,update,delete on nvendorsement.table_name to nonvehwrite;\n" +
                                    "create synonym nonvehwrite.table_name for nvendorsement.table_name;";
                        }
                        if ("统一工作台库".equals(database)) {
                            proEmpowermentSql = "grant select on nvportal.table_name to wushengrun_phq;\n" +
                                    "create synonym wushengrun_phq.table_name for nvportal.table_name;\n" +
                                    "grant select,insert,update,delete on nvportal.table_name to nonveh;\n" +
                                    "create synonym nonveh.table_name for nvportal.table_name;\n" +
                                    "grant select on nvportal.table_name to tangjunxiang_ghq;\n" +
                                    "create synonym tangjunxiang_ghq.table_name for nvportal.table_name;\n" +
                                    "grant select on nvportal.table_name to luweijun_ghq;\n" +
                                    "create synonym luweijun_ghq.table_name for nvportal.table_name;";

                            devEmpowermentSql = "grant select on nvportal.table_name to nonvehread;\n" +
                                    "create synonym nonvehread.table_name for nvportal.table_name;\n" +
                                    "grant select,insert,update,delete on nvportal.table_name to nonvehwrite;\n" +
                                    "create synonym nonvehwrite.table_name for nvportal.table_name;";
                        }

                        if ("汇总库".equals(database)) {


                            devEmpowermentSql = "grant select on nvpolicy.table_name to nonvehread;\n" +
                                    "create synonym nonvehread.table_name for nvpolicy.table_name;\n" +
                                    "grant select,insert,update,delete on nvpolicy.table_name to nonvehwrite;\n" +
                                    "create synonym nonvehwrite.table_name for nvpolicy.table_name;";
                        }


                        if (!"".equals(proEmpowermentSql)) {
                            proEmpowermentSql = "\r\n"+proEmpowermentSql.replaceAll("table_name", empowermentTable)+"\r\n";
                        }


                        if (!"".equals(devEmpowermentSql)) {
                            devEmpowermentSql = "\r\n"+devEmpowermentSql.replaceAll("table_name", empowermentTable)+"\r\n";
                        }


                        devEmpowermentSqlALL=devEmpowermentSqlALL+devEmpowermentSql;
                        proEmpowermentSqlALL=proEmpowermentSqlALL+proEmpowermentSql;
                    }
                    map.put("devEmpowermentSql", devEmpowermentSqlALL);
                    map.put("proEmpowermentSql", proEmpowermentSqlALL);
                    map.put("batchNo", batchNo);
                }

                execSql(optionDB, map);


            }

        }


    }


    public boolean execSql(OptionDB optionDB, List<String> sqlList) {
        JDBCUtil jdbcUtil = new JDBCUtil(optionDB);
        if (jdbcUtil == null) {
            System.out.println("链接数据库失败！");
            return false;
        }
        try {
            System.out.println("------"+optionDB.getUrl()+"---"+optionDB.getUsername());
            jdbcUtil.batchSql(sqlList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        } finally {
            jdbcUtil.commit();
        }

        return true;
    }

    public boolean execSql(OptionDB optionDB, Map<String, String> map) throws IOException {
        ArrayList list = new ArrayList();
        String sql = map.get("脚本").trim();

        if(!SQLUtils.checkSQLScript(sql)){
            return false;
        }

        String devEmpowermentSql = map.get("devEmpowermentSql");

        if (sql == null || "null".equals(sql)) {
            sql="";
        }
        if (devEmpowermentSql != null && !"".equals(devEmpowermentSql) && !"null".equals(devEmpowermentSql)) {
            sql = sql + "\r\n" + devEmpowermentSql;
        }
        if (sql.contains(";")) {
            String str[] = sql.split(";");
            for (String s : str) {
                s = s.trim();
                if(s!=null&&!"".equals(s)){
                    list.add(s);
                }

            }
        } else {
            list.add(sql);
        }

        if (execSql(optionDB, list)) {
            saveSqlFile(map);
            System.out.println(map.get("序号") + "已生成生产脚本");
            return true;

        } else {
            System.out.println(map.get("序号") + "执行出错，不生成脚本");
            return false;
        }

    }

    public boolean saveSqlFile(Map<String, String> map) throws IOException {
        if (map.get("发布计划名称").contains("灰度")){
            map.put("发布类型","灰度");
        }
        String fileName = "";
        fileName=fileName+map.get("脚本类型").trim().toUpperCase()+"_";
        fileName = fileName + map.get("发布计划名称") + "-";
        fileName = fileName + map.get("发布类型") + "-";
        fileName = fileName + map.get("数据库") + "-";
        fileName = fileName + map.get("提交人") + "-";
        fileName = fileName + map.get("序号").replace(".0", "") + "-";
        fileName = fileName + map.get("需求ID") + "-";
        fileName = fileName + map.get("需求名称") + ".sql";
        String content = map.get("脚本");
        String proEmpowermentSql = map.get("proEmpowermentSql");
        if (proEmpowermentSql != null && !"".equals(proEmpowermentSql) && !"null".equals(proEmpowermentSql)) {
            content = content + "\r\n" + proEmpowermentSql;
        }
        String path = "D:\\test\\temp\\" + map.get("发布计划名称");

        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdir();
        }

        if ("是".equals(map.get("是否为开关配置类脚本").trim())) {
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


    public static void main(String args[]) throws IOException {
        ExecuteSql sinoSingUtil = new ExecuteSql();
        //  sinoSingUtil.sqlBatch("D:\\test\\20211021\\管金龙2.xls");
        File dirFile=new File("D:\\test\\记录\\temp");

        File[] files=dirFile.listFiles();
        for(File file:files) {
            sinoSingUtil.sqlBatch(file);
        }

    }

}
