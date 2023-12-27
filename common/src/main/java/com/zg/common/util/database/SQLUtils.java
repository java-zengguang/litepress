package com.zg.common.util.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLUtils {


    public static List<String> splitSQL(String sourceSQL) {

        List<String> sqlList = new ArrayList<>();

        if (sourceSQL.contains(";")) {
            String[] str = sourceSQL.split(";");
            for (String s : str) {
                s = s.trim();
                if (s != null && !"".equals(s)) {
                    sqlList.add(s);
                }
            }
        } else {
            sqlList.add(sourceSQL);
        }
        return sqlList;
    }

    public static String formatSQL(String basesql, String owner) {
        String resultSQL = "";
        basesql = formatSQL(basesql);
        List<String> sqlList = splitSQL(basesql);
        for (String sql : sqlList) {
            String newSQL = "";
            List<String> list = Arrays.asList(sql.trim().split("\\s+"));
            String s1 = list.get(0).toLowerCase();
            String s2 = list.get(1).toLowerCase();
            String s3 = list.get(2).toLowerCase();
            if (s1.trim().contains("create") && "table".equals(s2.trim()) && !s3.contains(".")) {
                s3 = owner + "." + s3;//ddl语句为表名添加属主
                list.set(2, s3);
            }
            if (s1.trim().contains("alter") && "table".equals(s2.trim()) && !s3.contains(".")) {
                s3 = owner + "." + s3;//ddl语句为表名添加属主
                list.set(2, s3);
            }
            if (s1.trim().contains("update") && !s3.contains("set") && !s2.contains(".")) {
                s2 = owner + "." + s2;//ddl语句为表名添加属主
                list.set(1, s2);
            }
            if (s1.trim().contains("insert") && "into".equals(s2.trim()) && !s3.contains(".")) {
                s3 = owner + "." + s3;//dml语句为表名添加属主
                list.set(2, s3);
            }

            if (s1.trim().contains("delete") && "from".equals(s2.trim()) && !s3.contains(".")) {
                s3 = owner + "." + s3;//dml语句为表名添加属主
                list.set(2, s3);
            }

            if (s1.trim().contains("comment") && s2.trim().contains("on") && s3.trim().contains("column")) {
                String s4 = list.get(3).toLowerCase();
                if (!s4.contains(owner)) {
                    s4 = owner + "." + s4;
                }
                list.set(3, s4);
            }

            for (String s : list) {
                newSQL = newSQL + s + " ";
            }
            resultSQL = resultSQL + newSQL + ";\n";
        }


        return resultSQL;
    }

    //清洗脚本  去掉注释
    public static String formatSQL(String sourceSQL) {

        String[] lines = sourceSQL.split("\n");
        String exeSQL = "";
        for (String line : lines) {
            if (line.contains("--") && !line.contains(";")) {
                line = "";
            }
            exeSQL = exeSQL + " " + line;
        }

        sourceSQL = exeSQL + "\n";
        return sourceSQL;
    }


}






