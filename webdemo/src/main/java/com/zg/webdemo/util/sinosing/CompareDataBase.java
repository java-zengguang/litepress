package com.zg.webdemo.util.sinosing;

import com.zg.database.util.JDBCUtils;
import com.zg.handler.ProxyUtils;
import com.zg.util.io.POIUtils;
import com.zg.util.sinosing.JDBCUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.omg.CORBA.INTERNAL;
import org.python.antlr.ast.Str;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class CompareDataBase {

    Logger logger = Logger.getLogger(this.getClass().getName());
    String dir = "D:\\test\\数据对比\\temp";
    // String databaseNames[]={"保单库","投保单库","批单修改库","汇总库","统一工作台库","产品工厂库"};
    /*    String databaseNames[] = {"保单库", "投保单库", "批单修改库"};*/
    String databaseNames[] = {"保单库", "投保单库", "批单修改库", "汇总库"};
    //  String databaseNames[] = {"汇总库"};
    List<DatabaseTableStructureEntity> databaseTableStructureEntitieList = new ArrayList<>();
    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures");

    public CompareDataBase() {


    }

    private boolean load() throws Exception {
        List<String> sqlList = new ArrayList<>();
        //加载数据
        for (String databaseName : databaseNames) {
            String[] array = {"int", "uat", "stage"};
            for (String s : array) {
                List<DatabaseTableStructureEntity> stageList = GetDataStructure.getDataStructure(s, databaseName);
                databaseTableStructureEntitieList.addAll(stageList);
            }
            List<DatabaseTableStructureEntity> porList = GetDataStructure.getDataStructureByExcel(new File(dir), databaseName);
            databaseTableStructureEntitieList.addAll(porList);
            sqlList = GetDataStructure.readFileToSqlList(new File(dir), databaseName);
        }
        List<DatabaseTableStructureEntity> porList = GetDataStructure.getDataStructureByExcel(new File(dir), "老核心");
        databaseTableStructureEntitieList.addAll(porList);

        if (strcutureService.insertDataBaseTableStructures(databaseTableStructureEntitieList, sqlList)) {
            return true;
        } else {
            return false;
        }

    }


    private void getCompareExcel() {
        Map<String, List<Map>> resultMap = new HashMap<>();
        try {
            resultMap = strcutureService.getCompareResult();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateS = simpleDateFormat.format(new Date());
            String dir = "D:\\test\\数据对比\\temp\\result\\";
            String fileName = dateS + "数据结构对比结果.xlsx";
            POIUtils.writeXLSX(resultMap, new File(dir, fileName));
        } catch (SQLException | IOException | WriteException e) {
            e.printStackTrace();
        }
    }


    private void outPut() throws SQLException, IOException {
        List<Map> list = strcutureService.getDiffMap();
        if (list != null && list.size() > 0) {

        } else {
            logger.info("没有数据输出！");
        }

    }

    public List<Map> getMapList() throws SQLException, IOException, BiffException {
     /*  HSSFWorkbook hssfWorkbook=new HSSFWorkbook(new FileInputStream(new File("D:\\test\\生产拉齐.xsl"))) ;
       List<Map> mapList=  POIUtils.readExcel(hssfWorkbook,"sheet1");*/
        List<Map> list = strcutureService.getChangMapList();
        return list;
    }

    //List<Map> databasename tablename  columnname columntype
    private void getAlertSQL(List<Map> mapList) {
        List list1 = new ArrayList(); //保单库脚本
        List list2 = new ArrayList(); //投保单库脚本
        List list3 = new ArrayList(); //批单修改库脚本
        String sql1 = "";
        String sql2 = "";
        String sql3 = "";

        for (Map map : mapList) {
            String oldColumnType = ((String) map.get("oldcolumntype")).toUpperCase();
            String columnType = ((String) map.get("columntype")).toUpperCase();
            if (columnType.contains("VARCHAR2") && !columnType.substring(0, columnType.indexOf("(")).equals(oldColumnType.substring(0, oldColumnType.indexOf("(")))) {
                System.out.println("脚本中涉及字段类型变更，操作失败！");
                return;
            }
            if (columnType.contains("VARCHAR2")) {
                String columnTypeStr = columnType.replace("VARCHAR2", "").replace("(", "").replace(")", "");
                String oldColumnTypeStr = oldColumnType.replace("VARCHAR2", "").replace("(", "").replace(")", "");

                Integer columnLength = Integer.parseInt(columnTypeStr);
                Integer oldColumnLength = Integer.parseInt(oldColumnTypeStr);
                if (columnLength < oldColumnLength) {
                    System.out.println("字段长度只能增加不能减少" + map.get("columnname"));
                    return;
                }
            }

            String sql = "alter table " + (String) map.get("tablename") + " modify " + (String) map.get("columnname") + "  " + (String) map.get("columntype") + " ;\r\n";
            if (map.get("databasename").equals("保单库")) {
                list1.add(sql);
                sql1 = sql1 + sql;
            }
            if (map.get("databasename").equals("投保单库")) {
                list2.add(sql);
                sql2 = sql2 + sql;
            }
            if (map.get("databasename").equals("批单修改库")) {
                list3.add(sql);
                sql3 = sql3 + sql;
            }
        }

        System.out.println("Sql1:-----");
        System.out.println(sql1);
        System.out.println("Sql2:-----");
        System.out.println(sql2);
        System.out.println("Sql3:-----");
        System.out.println(sql3);


    }

    public static void main(String args[]) throws SQLException, IOException, BiffException {

        CompareDataBase compareDataBase = new CompareDataBase();
        try {
            compareDataBase.load();
            compareDataBase.getCompareExcel();
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        CompareDataBase compareDataBase = new CompareDataBase();
       // List<Map> maps = compareDataBase.getMapList();
        List<Map> maps=JDBCUtils.selectToMapList("select d.databaseName as databasename,d.tablename ,d.columnname ,d.columntype ,d.columntype as oldcolumntype from test a,databasetablestructure d where a.batchno ='5' and d.tablename like a.tablename and a.columnname =d.columnname and d.environment ='pro'" );
        compareDataBase.getAlertSQL(maps);
*/

/*        List<Map> maps= POIUtils.readExcel(new HSSFWorkbook(new FileInputStream(new File("D:\\test\\数据对比\\20211118生产表结构拉齐\\套表拉齐\\新核心第一批.xls"))),"sheet1");
        CompareDataBase compareDataBase = new CompareDataBase();
        List<Map> mapList=new ArrayList<>();
        for(Map map: maps){
            String sql ="select d.databasename ,d.tablename ,d.columnname ,d.columntype as oldcolumntype,('"+map.get("columntype")+"') as columntype from databasetablestructure d where d.environment ='pro'" +
                    " and ( d.tablename = 'PRPC"+map.get("tablename")+"' or d.tablename = 'PRPT"+map.get("tablename")+"'  or d.tablename = 'PRPCP"+map.get("tablename")+"'" +
                    "or d.tablename = 'PRPCOPY"+map.get("tablename")+"' or d.tablename = 'PRPP"+map.get("tablename")+"' ) and d.columnname ='"+map.get("columnname")+"' ";
            mapList.addAll(JDBCUtils.selectToMapList(sql));
        }
        compareDataBase.getAlertSQL(mapList);*/
    }
}
