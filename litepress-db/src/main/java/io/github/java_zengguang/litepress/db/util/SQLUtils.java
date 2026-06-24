package io.github.java_zengguang.litepress.db.util;

import java.util.ArrayList;
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






