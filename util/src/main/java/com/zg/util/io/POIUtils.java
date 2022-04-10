package com.zg.util.io;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.python.antlr.ast.Str;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2019/3/5 0005.
 */
public class POIUtils {


    public static void wirteXLS(Map<String, List<Map>> map, File inputFile, File outputFile) throws IOException {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(inputFile));
        for (String key : map.keySet()) {
            POIUtils.addSheet(hssfWorkbook, key, map.get(key), 1, 1);
        }
        OutputStream outputStream = new FileOutputStream(outputFile);
        hssfWorkbook.write(outputStream);
    }

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

    //返回第一行为表头
    public static List<Map> readExcel(HSSFWorkbook hssfWorkbook, String sheetName) {
        List list = new ArrayList();
        HSSFSheet sheet = hssfWorkbook.getSheet(sheetName);
        HSSFRow firstRow = sheet.getRow(sheet.getFirstRowNum());
        for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum() + 1; i++) {

            HSSFRow row = sheet.getRow(i);
            Map map = new HashMap();
            for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                if (firstRow.getCell(j) != null && row.getCell(j) != null) {
                    map.put(firstRow.getCell(j).toString().trim(), row.getCell(j).toString().trim());
                }
            }
            list.add(map);
        }
        return list;
    }

    //将excel映射到内存中 Sheet,<TableList<Map<String,String>>>
    public static Map<String, List<Map<String, String>>> readXLSX(File file) throws IOException, BiffException {
        Map<String, List<Map<String, String>>> resultMap = new HashMap();
        Workbook workbook = Workbook.getWorkbook(file);
        Sheet[] sheets = workbook.getSheets();
        for (Sheet sheet : sheets) {
            String sheetName = sheet.getName();
            Cell[] titleRow = sheet.getRow(0);
            List<Map<String, String>> tableList = new ArrayList();
            for (int i = 1; i < sheet.getRows() + 1; i++) {
                Map<String, String> lineMap = new HashMap<>();
                for (int j = 0; j < sheet.getColumns(); j++) {
                    lineMap.put(titleRow[j].getContents().trim(), sheet.getCell(i, j).getContents().trim());
                }
                tableList.add(lineMap);
            }
            resultMap.put(sheetName, tableList);
        }
        return resultMap;
    }


    public static boolean writeXLSX(Map<String, List<Map>> mapList, File file) throws IOException, WriteException {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return false;
            }
        }
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        Set<String> keySet = mapList.keySet();
        int i = 0;
        for (String key : keySet) {
            int j = 0;
            List<Map> list = mapList.get(key);
            WritableSheet sheet = workbook.createSheet(key, i);
            sheet.getSettings().setDefaultColumnWidth(20);
            for (Map<String, String> map : list) {
                int x = 0;
                Set<String> columnSet = map.keySet();
                for (String column : columnSet) {
                    WritableCell cell;
                    String content=String.valueOf(map.get(column));
                    cell = new Label(x, j, content);
                    sheet.addCell(cell);
                    x++;
                }
                j++;
            }
            //自动调整列宽
            i++;
        }

        workbook.write();
        workbook.close();
        return true;
    }

    public static void main(String args[]) throws SQLException, IOException, ClassNotFoundException {


    }
}
