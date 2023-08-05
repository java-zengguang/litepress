/*
package com.zg.sqlcheck.common.util;

import com.zg.common.proxy.ProxyUtils;
import com.zg.common.util.io.POIUtils;

import com.zg.sqlcheck.model.entity.DatabaseTableStructureEntity;
import com.zg.sqlcheck.common.service.DatabaseTableStrcutureService;
import com.zg.sqlcheck.common.service.DatabaseTableStrcutureServiceImpl;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class CompareDataBase {

    Logger logger = Logger.getLogger(this.getClass().getName());
    String dir = "D:\\test\\数据对比\\temp";
    // String databaseNames[]={"保单库","投保单库","批单修改库","汇总库","统一工作台库","产品工厂库"};
    */
/*    String databaseNames[] = {"保单库", "投保单库", "批单修改库"};*//*


    //  String databaseNames[] = {"汇总库"};
    List<DatabaseTableStructureEntity> databaseTableStructureEntitieList = new ArrayList<>();
    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures");

    public CompareDataBase() {


    }

    public static void main(String args[]) throws SQLException, IOException, BiffException {

        CompareDataBase compareDataBase = new CompareDataBase();
        try {
            //  compareDataBase.loadBaseTable();
           // compareDataBase.load();
            compareDataBase.getCompareExcel();
        } catch (Exception e) {
            Logger.error(e);
        }

    }


    private boolean load() throws Exception {
        List<String> sqlList = new ArrayList<>();
        //加载数据
        //新一代数据加载
        if (false) {

            //  String databaseNames[] = {"nvpolicy", "nvproposal", "nvendorsement", "nvsun"};
            String databaseNames[] = {"保单库", "批单修改库", "投保单库"};
            for (String databaseName : databaseNames) {
                //  String[] array = {"int", "uat", "stage"};
                String[] array = {"int", "uat", "stage"};
                for (String s : array) {
                    List<DatabaseTableStructureEntity> stageList = GetDataStructure.getDataStructure(s, databaseName, "new-non-auto");
                    databaseTableStructureEntitieList.addAll(stageList);
                }
                //   sqlList = GetDataStructure.readFileToSqlList(new File(dirs), databaseName);
            }

        }

        if (false) {
            List<DatabaseTableStructureEntity> stageList = GetDataStructure.getDataStructure("dev", "平台库", "platform");
            databaseTableStructureEntitieList.addAll(stageList);
        }

        //生产数据加载
        if (true) {
            String dirs = dir;
            List<DatabaseTableStructureEntity> porList = GetDataStructure.getDataStructureByExcel(new File(dirs));
            databaseTableStructureEntitieList.addAll(porList);
        }

        //老核心测试
        if (false) {


            //   String[] array = { "投保单库","保单库"};
            String[] array = {"保单库"};
            for (String s : array) {
                List<DatabaseTableStructureEntity> stageList = GetDataStructure.getDataStructure("dev", s, "old-non-auto");
                databaseTableStructureEntitieList.addAll(stageList);
            }
        }

        //测试新一代库
        if (false) {
            String databaseName = "汇总库";
            String[] array = {"int", "uat", "stage"};
            for (String s : array) {
                List<DatabaseTableStructureEntity> stageList = GetDataStructure.getDataStructure(s, databaseName, "new-non-auto");
                databaseTableStructureEntitieList.addAll(stageList);
            }
        }

        if (strcutureService.insertDataBaseTableStructures(databaseTableStructureEntitieList, sqlList)) {
            return true;
        } else {
            return false;
        }

    }

    private void getCompareExcel() {
        Map<String, List<Map>> resultMap = new HashMap<>();
        try {
            resultMap = strcutureService.getCompareResult("CheckPro");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateS = simpleDateFormat.format(new Date());
            String dir = "D:\\test\\数据对比\\result\\";
            String fileName = dateS + "数据结构对比结果.xlsx";
            POIUtils.writeXLSX(resultMap, new File(dir, fileName));
        } catch (SQLException | IOException | WriteException | ClassNotFoundException e) {
            Logger.error(e);
        }
    }
}
*/
