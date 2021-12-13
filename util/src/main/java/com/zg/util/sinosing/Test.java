package com.zg.util.sinosing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class Test {


    public boolean saveSqlFile(Map<String, String> map) throws IOException {
        if (map.get("发布计划名称").contains("灰度")){
            map.put("发布类型","灰度");
        }
        String fileName = "";
        fileName=fileName+map.get("脚本类型").trim().toUpperCase()+"_";
        fileName = fileName + map.get("发布计划名称") + "-";
        fileName = fileName + map.get("发布类型") + "-";
        fileName = fileName + map.get("数据库") + "-";
        fileName = fileName + map.get("提交人") + "-";
        fileName = fileName + map.get("序号").replace(".0", "") + "-";
        fileName = fileName + map.get("需求ID") + "-";
        fileName = fileName + map.get("需求名称") + ".sql";
        String content = map.get("脚本");
        String proEmpowermentSql = map.get("proEmpowermentSql");
        if (proEmpowermentSql != null && !"".equals(proEmpowermentSql) && !"null".equals(proEmpowermentSql)) {
            content = content + "\r\n" + proEmpowermentSql;
        }
        String path = "D:\\test\\temp\\" + map.get("发布计划名称");

        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdir();
        }

        if ("是".equals(map.get("是否为开关配置类脚本").trim())) {
            path = path + "\\开关管理";
        } else {
            path = path + "\\脚本管理";
        }

        pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdir();
        }

        File file = new File(path, fileName);

        if (file.exists()) {
            file.createNewFile();
        }
        Writer writer = new FileWriter(file);
        writer.write(content);
        writer.flush();
        writer.close();
        return true;
    }

}


