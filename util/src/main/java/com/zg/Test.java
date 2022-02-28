package com.zg;

import com.zg.util.io.POIUtils;
import jxl.write.WriteException;
import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {


    public static List<List> splitList(List list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }

        List<List> result = new ArrayList<List>();


        int size = list.size();
        int count = (size + len - 1) / len;


        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }


    public static void main(String args[]) throws IOException, WriteException {
        List<Map> list=new ArrayList<>();
        File file= new File("D:\\test\\新建文件夹\\问题.xls");
        InputStream inputStream = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        list=POIUtils.readExcel(hssfWorkbook, "SQL Results");
        Map<String,List<Map>> map=new HashMap<>();

        List<List> resultList=splitList(list,1000);
        for(int i=0;i<resultList.size();i++){
            List<Map> x=resultList.get(i);
            map.put("sheet"+i,x);
        }
        POIUtils.writeXLSX(map,new File("D:\\test\\新建文件夹\\问题.xlsx"));
    }
}
