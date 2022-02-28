package com.zg.sinosig.report;

import com.sinosig.saab.util.DateUtil;
import com.zg.util.io.POIUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class SimpleReportBataBase implements ReportDataBase {
    private String executeRoot;

    private String name = "zengguang-phq";
    private String password = "";

    public SimpleReportBataBase(String executeRoot) {
        this.executeRoot = executeRoot;
    }

    private boolean saveSqlFile(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws IOException {

        String fileName = "";
        fileName = fileName + sinoSigSQLLogEntity.serialno.replace(".0", "") + "-";
        fileName = fileName + sinoSigSQLLogEntity.sqltype.trim().toUpperCase() + "_";
        fileName = fileName + sinoSigSQLLogEntity.sqlpurpose + "_";
        fileName = fileName + sinoSigSQLLogEntity.databasename + "-";
        fileName = fileName + sinoSigSQLLogEntity.planname + "-";
        fileName = fileName + sinoSigSQLLogEntity.applyusername + "-";
        fileName = fileName + sinoSigSQLLogEntity.demandname + ".sql";
        String content = sinoSigSQLLogEntity.prosql;
        Calendar cal = Calendar.getInstance();
        String systemFlag=sinoSigSQLLogEntity.systemflag;
        String systemName="";
        switch (systemFlag) {
            case "new-non-auto": {
                systemName = "新一代脚本";
                break;
            }
            case "platform": {
                systemName = "平台配置脚本";
                break;
            }
            case "old-non-auto": {
                systemName = "老核心脚本";
                break;
            }
        }

        String path = executeRoot + "temp\\" + sinoSigSQLLogEntity.planname;

        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdir();
        }

        path=path+"\\"+systemName;
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


    private void outPut(List<SinoSigSQLLogEntity> list) throws Exception {
        Boolean flag = true;
        //生成脚本
        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            //有失败的触发邮件通知
            if ("-1".equals(sinoSigSQLLogEntity.executestate)) {
                flag = false;
            }
            if ("3".equals(sinoSigSQLLogEntity.executestate)) {
                try {
                    saveSqlFile(sinoSigSQLLogEntity);
                } catch (IOException e) {
                    e.printStackTrace();
                    sinoSigSQLLogEntity.setErrormassage("生成脚本失败" + e.getMessage());
                    continue;
                }
            }
        }
        //组装结果excel
        Map<String, List<Map>> resultMap = new HashMap<>();
        List<Map> mapList = new ArrayList<>();
        if (true) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("批次号", "批次号");
            map.put("序号", "序号");
            map.put("发布计划", "发布计划");
            map.put("需求ID", "需求ID");
            map.put("提交人", "提交人");
            map.put("执行环境", "执行环境");
            map.put("执行状态", "执行状态");
            map.put("错误信息", "错误信息");
            map.put("提交脚本", "提交脚本");
            mapList.add(map);
        }
        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {

            Map<String, String> map = new LinkedHashMap<>();
            map.put("批次号", sinoSigSQLLogEntity.batchno);
            map.put("序号", sinoSigSQLLogEntity.serialno);
            map.put("发布计划", sinoSigSQLLogEntity.planname);
            map.put("需求ID", sinoSigSQLLogEntity.demandid);
            map.put("提交人", sinoSigSQLLogEntity.applyusername);
            map.put("执行环境", sinoSigSQLLogEntity.environment);
            map.put("执行状态", sinoSigSQLLogEntity.executestate);
            map.put("错误信息", sinoSigSQLLogEntity.errormassage);
            map.put("提交脚本", sinoSigSQLLogEntity.basesql);
            mapList.add(map);
        }
        resultMap.put("执行结果情况", mapList);

        //执行结构
        String fileName=DateUtil.format(new Date(),"yyyy-MM-dd-HHmmss") + ".xlsx";
        File resultFile = new File(executeRoot + "out" ,fileName);
        POIUtils.writeXLSX(resultMap, resultFile);

    }

    @Override
    public void doResult(List<SinoSigSQLLogEntity> list) {
        try {
            outPut(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
