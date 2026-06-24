package io.github.java_zengguang.litepress.core.util.io.poi;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    public static XSSFWorkbook addSheet(XSSFWorkbook hssfWorkbook, String sheetName, List<Map> list, int topLine, int leftColumn) {
        XSSFSheet sheet = hssfWorkbook.createSheet(sheetName);
        XSSFRow hssfRowHead = null;

        for (int i = 0; i < list.size(); i++) {

            if (i == 0) {
                hssfRowHead = sheet.createRow(topLine);  //创建表头
            }
            Map<String, String> map = list.get(i);


            XSSFRow hssfRow = sheet.createRow(i + topLine + 1);  //一行表头
            Set<String> keySet = map.keySet();
            int j = 0;
            for (String key : keySet) {
                if (i == 0) {    //第一行写入表头
                    XSSFCell headCell = hssfRowHead.createCell(j + leftColumn);
                    headCell.setCellValue(key);

                }
                XSSFCell hssfCell = hssfRow.createCell(j + leftColumn);
                hssfCell.setCellValue(map.get(key));

                j++;
            }

        }

        return hssfWorkbook;

    }

    public static void writeXLSX(Map<String, List<Map>> map, File file) throws IOException {
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook();
        for (String key : map.keySet()) {
            POIUtils.addSheet(hssfWorkbook, key, map.get(key), 0, 0);
        }
        OutputStream outputStream = new FileOutputStream(file);
        hssfWorkbook.write(outputStream);
    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {


    }
}
