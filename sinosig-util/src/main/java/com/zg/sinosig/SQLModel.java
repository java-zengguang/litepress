package com.zg.sinosig;

import com.zg.sinosig.check.CheckSQL;
import com.zg.sinosig.check.SimpleCheckSQL;
import com.zg.sinosig.execute.ExecuteSQL;
import com.zg.sinosig.execute.SimpleExecute;
import com.zg.sinosig.generate.GenerateSQL;
import com.zg.sinosig.generate.SimpleGeneraterSQL;
import com.zg.sinosig.load.LoadDataBaseStructure;
import com.zg.sinosig.load.SimpleLoadDataBaseStructure;
import com.zg.util.io.POIUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SQLModel {

    //  private final String rootPath = FileUtils.PATH;
    private final String rootPath;
    private CheckSQL checkSQL = new SimpleCheckSQL();
    private ExecuteSQL executeSQL = new SimpleExecute();
    private LoadDataBaseStructure loadDataBaseStructure;

    public SQLModel() {
        this.rootPath = "D:\\test\\SQLExcute\\";
        this.loadDataBaseStructure = new SimpleLoadDataBaseStructure(rootPath + "in");
    }

    public SQLModel(String rootPath) {
        this.rootPath = rootPath;
        this.loadDataBaseStructure = new SimpleLoadDataBaseStructure(rootPath + "in");

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
        int year = cal.get(Calendar.YEAR);
        String path = rootPath + "temp\\" + sinoSigSQLLogEntity.planname;

        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdir();
        }

        if ("是".equals(sinoSigSQLLogEntity.isconfig.trim())) {
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


    private void writeResultExcel(List<SinoSigSQLLogEntity> list, File outFile) {

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String dateStr = simpleDateFormat.format(new Date());
        File resultFile = new File(rootPath + "out\\" + dateStr + ".xlsx");
        POIUtils.writeXLSX(resultMap, resultFile);
    }




    public void domain() throws Exception {

        File dirFile = new File(rootPath + "in\\");
        String executeBatchNo = "" + (new Date()).getTime();
        List<SinoSigSQLLogEntity> resultList = new ArrayList<>();
        //uat 执行
        if (true) {
            GenerateSQL generateSQL = new SimpleGeneraterSQL(dirFile, executeBatchNo, "uat");
            List<SinoSigSQLLogEntity> list = generateSQL.initLoadSinoSigSQL();
            list = checkSQL.checkSQL(list);
             list= executeSQL.excute(list);
            resultList.addAll(list);
        }
        //stage 执行
        if (true) {
            GenerateSQL generateSQL = new SimpleGeneraterSQL(dirFile, executeBatchNo, "stage");
            List<SinoSigSQLLogEntity> list = generateSQL.initLoadSinoSigSQL();
            list = checkSQL.checkSQL(list);
             list = executeSQL.excute(list);
            resultList.addAll(list);
        }
        outPut(resultList);
    }





    public static void main(String args[]) throws Exception {
        SQLModel sqlModel = new SQLModel();
        sqlModel.domain();
    }
}
