package com.zg.sinosig.load;

import com.zg.handler.ProxyUtils;
import com.zg.util.reflect.ClassUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.entity.LDCode;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;
import com.zg.webdemo.service.ldcode.LDCodeService;
import com.zg.webdemo.service.ldcode.LDCodeServiceImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleLoadDataBaseStructure implements LoadDataBaseStructure {
    private String rootDir = "";
    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures,reloadDataBaseTableStructures");
    LDCodeService ldCodeService = (LDCodeService) ProxyUtils.getProxyClass(new LDCodeServiceImpl(), "reLoadPRPTable");

    public SimpleLoadDataBaseStructure() {

    }

    public SimpleLoadDataBaseStructure(String rootDir) {
        this.rootDir = rootDir;
    }


    private boolean reloadBaseTable() {
        String packageName = "com.sinosig.nonveh.model.prpbase";
        Set<Class<?>> classSet = ClassUtil.getClasses(packageName);
        List<LDCode> tableNameList = new ArrayList<>();
        for (Class classes : classSet) {
            String baseTableName = classes.getSimpleName();
            String baseName = baseTableName.replaceFirst("Prp", "");
            tableNameList.add(new LDCode("prp_table", "PRPC" + baseName.toUpperCase(), "保单库", baseName.toUpperCase()));
            tableNameList.add(new LDCode("prp_table", "PRPCOPY" + baseName.toUpperCase(), "保单库", baseName.toUpperCase()));
            tableNameList.add(new LDCode("prp_table", "PRPP" + baseName.toUpperCase(), "保单库", baseName.toUpperCase()));
            tableNameList.add(new LDCode("prp_table", "PRPCP" + baseName.toUpperCase(), "批单修改库", baseName.toUpperCase()));
            tableNameList.add(new LDCode("prp_table", "PRPCOPY" + baseName.toUpperCase(), "批单修改库", baseName.toUpperCase()));
            tableNameList.add(new LDCode("prp_table", "PRPP" + baseName.toUpperCase(), "批单修改库", baseName.toUpperCase()));
            tableNameList.add(new LDCode("prp_table", "PRPT" + baseName.toUpperCase(), "投保单库", baseName.toUpperCase()));
        }
        try {
            ldCodeService.reLoadPRPTable(tableNameList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean reloadDatabaseStrucure() throws Exception {

        List<DatabaseTableStructureEntity> databaseTableStructureEntitieList = new ArrayList<>();
        //加载数据
        //新一代数据加载
        if (true) {
            String dirs = rootDir + "\\新一代";
            String databaseNames[] = {"保单库", "批单修改库", "投保单库"};
            for (String databaseName : databaseNames) {
                String[] array = {"int", "uat", "stage"};
                for (String s : array) {
                    List<DatabaseTableStructureEntity> stageList = GetDataStructure.getDataStructure(s, databaseName, "new-non-auto");
                    databaseTableStructureEntitieList.addAll(stageList);
                }

            }
            List<DatabaseTableStructureEntity> porList = GetDataStructure.getDataStructureByExcel(new File(dirs),"new-non-auto");
            databaseTableStructureEntitieList.addAll(porList);
        }
        //老核心数据加载
        if (true) {
            String dirs = rootDir + "\\老核心";
            String databaseNames[] = {"保单库", "投保单库"};
            for (String databaseName : databaseNames) {
                String[] array = {"old_dev"};
                for (String s : array) {
                    List<DatabaseTableStructureEntity> stageList = GetDataStructure.getDataStructure(s, databaseName, "old-non-auto");
                    databaseTableStructureEntitieList.addAll(stageList);
                }
            }
            List<DatabaseTableStructureEntity> porList = GetDataStructure.getDataStructureByExcel(new File(dirs),"old-non-auto");
            databaseTableStructureEntitieList.addAll(porList);
        }

        //平台测试库加载
        if (true) {

            List<DatabaseTableStructureEntity> stageList = GetDataStructure.getDataStructure("dev", "平台库", "platform");
            databaseTableStructureEntitieList.addAll(stageList);
        }


        //重新加载表结构
        if (strcutureService.reloadDataBaseTableStructures(databaseTableStructureEntitieList)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean reLoadStructure(List<SinoSigSQLLogEntity> list) throws Exception {

        Set<String> environmentFlagSet = new HashSet<>();
        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            if ("DDL".equals(sinoSigSQLLogEntity.sqltype) && "3".equals(sinoSigSQLLogEntity.executestate)) {
                environmentFlagSet.add(sinoSigSQLLogEntity.environment+","+sinoSigSQLLogEntity.systemflag);
            }
        }
        for (String environmentFlag : environmentFlagSet) {
            reLoadStructure(environmentFlag);
        }
        return false;
    }

    @Override
    public boolean loadStructure() throws Exception {
        if (reloadBaseTable() && reloadDatabaseStrucure()) {
            return true;
        } else {
            return false;
        }
    }


    private boolean reLoadStructure(String environmentFlag) throws Exception {
        String[] strings=environmentFlag.split(",");
        strcutureService.reloadDataBaseTableStructures(strings[0],strings[1]);
        return false;
    }


    public static void main(String args[]) throws Exception {
        SimpleLoadDataBaseStructure simpleLoadDataBaseStructure = new SimpleLoadDataBaseStructure();
        simpleLoadDataBaseStructure.reLoadStructure("int,new-non-auto");
    }

}
