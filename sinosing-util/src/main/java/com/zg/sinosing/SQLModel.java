package com.zg.sinosing;

import com.zg.sinosing.check.CheckSQL;
import com.zg.sinosing.check.SimpleCheckSQL;
import com.zg.sinosing.excute.ExcuteSQL;
import com.zg.sinosing.excute.SimpleExcute;
import com.zg.sinosing.generate.GenerateSQL;
import com.zg.sinosing.generate.SimpleGeneraterSQL;
import com.zg.sinosing.load.LoadDataBaseStructure;
import com.zg.sinosing.load.SimpleLoadDataBaseStructure;
import com.zg.util.io.POIUtils;
import com.zg.webdemo.entity.SinoSingSQLLogEntity;
import jxl.write.WriteException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

public class SQLModel {

    private final String rootPath = "D:\\test\\SQLExcute\\";
    private CheckSQL checkSQL = new SimpleCheckSQL();
    private ExcuteSQL excuteSQL = new SimpleExcute();
    private LoadDataBaseStructure loadDataBaseStructure = new SimpleLoadDataBaseStructure("D:\\test\\SQLExcute\\in");


    private boolean saveSqlFile(SinoSingSQLLogEntity sinoSingSQLLogEntity) throws IOException {

        String fileName = "";
        fileName = fileName + sinoSingSQLLogEntity.serialno.replace(".0", "") + "-";
        fileName = fileName + sinoSingSQLLogEntity.sqltype.trim().toUpperCase() + "_";
        fileName = fileName + sinoSingSQLLogEntity.sqlpurpose + "_";
        fileName = fileName + sinoSingSQLLogEntity.databasename + "-";
        fileName = fileName + sinoSingSQLLogEntity.planname + "-";
        fileName = fileName + sinoSingSQLLogEntity.applyusername + "-";
        fileName = fileName + sinoSingSQLLogEntity.demandname + ".sql";
        String content = sinoSingSQLLogEntity.prosql;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        String path = rootPath + "temp\\"  + sinoSingSQLLogEntity.planname;

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


    private void writeResultExcel(List<SinoSingSQLLogEntity> list, File outFile) {

    }

    private void outPut(List<SinoSingSQLLogEntity> list) throws WriteException, IOException {
        //生成脚本
        for (SinoSingSQLLogEntity sinoSingSQLLogEntity : list) {
            if ("3".equals(sinoSingSQLLogEntity.executestate)) {
                try {
                    saveSqlFile(sinoSingSQLLogEntity);
                } catch (IOException e) {
                    e.printStackTrace();
                    sinoSingSQLLogEntity.setErrormassage("生成脚本失败" + e.getMessage());
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
        for (SinoSingSQLLogEntity sinoSingSQLLogEntity : list) {

            Map<String, String> map = new LinkedHashMap<>();
            map.put("批次号", sinoSingSQLLogEntity.batchno);
            map.put("序号", sinoSingSQLLogEntity.serialno);
            map.put("发布计划", sinoSingSQLLogEntity.planname);
            map.put("需求ID", sinoSingSQLLogEntity.demandid);
            map.put("提交人", sinoSingSQLLogEntity.applyusername);
            map.put("执行环境", sinoSingSQLLogEntity.environment);
            map.put("执行状态", sinoSingSQLLogEntity.executestate);
            map.put("错误信息", sinoSingSQLLogEntity.errormassage);
            map.put("提交脚本", sinoSingSQLLogEntity.basesql);
            mapList.add(map);
        }


        resultMap.put("执行结果情况", mapList);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String dateStr = simpleDateFormat.format(new Date());
        File resultFile = new File(rootPath + "out\\" + dateStr + ".xlsx");
        POIUtils.writeXLSX(resultMap, resultFile);

    }


    private void domain() throws Exception {

        File dirFile = new File(rootPath + "in\\");
        String executeBatchNo = "" + (new Date()).getTime();
        List<SinoSingSQLLogEntity> resultList = new ArrayList<>();
        //uat 执行
        if (true) {
            GenerateSQL generateSQL = new SimpleGeneraterSQL(dirFile, executeBatchNo, "uat");
            List<SinoSingSQLLogEntity> list = generateSQL.initLoadSinoSingSQL();
            list = checkSQL.checkSQL(list);
            //list= excuteSQL.excute(list);
            resultList.addAll(list);
        }
        //stage 执行
        if (false) {
            GenerateSQL generateSQL = new SimpleGeneraterSQL(dirFile, executeBatchNo, "stage");
            List<SinoSingSQLLogEntity> list = generateSQL.initLoadSinoSingSQL();
            list = checkSQL.checkSQL(list);
            // list = excuteSQL.excute(list);
            resultList.addAll(list);
        }
        outPut(resultList);

    }

    public static void main(String args[]) throws Exception {
        SQLModel sqlModel = new SQLModel();
        sqlModel.domain();
    }
}
