package com.zg.util.io;

import com.zg.database.util.JDBCUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2019/3/5 0005.
 */
public class POIUtils {



    public static HSSFWorkbook addSheet(HSSFWorkbook hssfWorkbook, String sheetName, List<Map> list, int topLine, int leftColumn) {
        HSSFSheet sheet = hssfWorkbook.createSheet(sheetName);
        HSSFRow hssfRowHead = null;

        for (int i = 0; i < list.size(); i++) {

            if (i == 0) {
                hssfRowHead = sheet.createRow(topLine);  //创建表头
            }
            Map<String, String> map = list.get(i);



            HSSFRow hssfRow = sheet.createRow(i + topLine + 1);  //一行表头
            Set<String> keySet = map.keySet();
            int j = 0;
            for (String key : keySet) {
                if (i == 0) {    //第一行写入表头
                    HSSFCell headCell = hssfRowHead.createCell(j + leftColumn);
                    headCell.setCellValue(key);

                }
                HSSFCell hssfCell = hssfRow.createCell(j + leftColumn);
                hssfCell.setCellValue(map.get(key));

                j++;
            }

        }

        return hssfWorkbook;

    }


    public static List<Map> readExcel(HSSFWorkbook hssfWorkbook,String sheetName){
        List list=new ArrayList();
       HSSFSheet sheet= hssfWorkbook.getSheet(sheetName);
        HSSFRow firstRow=sheet.getRow(sheet.getFirstRowNum());
       for(int i=sheet.getFirstRowNum();i<sheet.getLastRowNum();i++) {

           HSSFRow row = sheet.getRow(i);
           Map map=new HashMap();
           for (int j=row.getFirstCellNum();j<row.getLastCellNum();j++){
              map.put(firstRow.getCell(j),row.getCell(j));
           }
           list.add(map);
       }
       return list;
    }

    public static void main(String args[]) throws SQLException, IOException {
        String sql = "select *from user_login ";
        List list = JDBCUtils.selectToMapList(sql);
       // File file = new File("f:\\q.xls");
       // FileOutputStream out = new FileOutputStream(file);
        HSSFWorkbook hssfWorkbook =new HSSFWorkbook();
        addSheet(hssfWorkbook, "test", list,5,5);
        List list1=readExcel(hssfWorkbook,"test");
        System.out.println("list1="+list1);
       // hssfWorkbook.write(out);


    }
}
