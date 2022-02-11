package com.zg.sinosig.introduce;

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

public class SimpleIntroduceSQL implements IntroduceSQL {


    private String executeRoot;
    private String executeBatchNo;

   // private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyClass(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog");

    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(),"insertSinoSingSQLLog,updateStateSinoSingSQLLog"));

    public SimpleIntroduceSQL(String executeRoot, String executeBatchNo) {
        this.executeRoot = executeRoot;
        this.executeBatchNo = executeBatchNo;
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
                String[] sheetNames = {"新一代脚本", "平台配置脚本", "老核心脚本"};
                Random random = new Random();
                String batchNo = "JB" + new Date().getTime() + random.nextInt();
                for (String sheetName : sheetNames) {
                    List<Map> mapList = POIUtils.readExcel(hssfWorkbook, sheetName);


                    String systemFlag = "";
                    switch (sheetName) {
                        case "新一代脚本": {
                            systemFlag = "new-non-auto";
                            break;
                        }
                        case "平台配置脚本": {
                            systemFlag = "platform";
                            break;
                        }
                        case "老核心脚本": {
                            systemFlag = "old-non-auto";
                            break;
                        }
                    }
                    for (int i = 1; i < mapList.size(); i++) {
                        Map<String, String> map = mapList.get(i);
                        if (map.get("序号") != null && !"".equals(map.get("序号").trim())) {

                            SinoSigSQLLogEntity baseEntity = new SinoSigSQLLogEntity();
                            baseEntity.batchno = batchNo;
                            baseEntity.serialno = map.get("序号").trim();
                            baseEntity.executestate = "0";
                            baseEntity.executebatchno = executeBatchNo;
                            baseEntity.systemflag = systemFlag;
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

                            logEntities.add(baseEntity);
                         //   sinoSigSQLLogService.insertSinoSingSQLLog(baseEntity);
                        }

                    }
                }
            }

        }
        return logEntities;
    }
    @Override
    public List initLoadSinoSigSQL() {
        List<SinoSigSQLLogEntity> list = new ArrayList();
        try {
            File dirFile = new File(executeRoot + "in\\");
            list = generateSQLFromExcel(dirFile);
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
