package com.zg.sinosig.load;

import com.zg.database.util.JDBCUtils;
import com.zg.util.io.POIUtils;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.python.antlr.ast.Str;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GetDataStructure {

    public static List<String> getOwner(String environment, String database, String systemFlag) {
        List<String> owners = new ArrayList<>();
        if ("new-non-auto".equals(systemFlag)) {
            switch (database) {
                case "保单库": {
                    owners = Arrays.asList("nvpolicy");
                    break;
                }
                case "投保单库": {
                    owners = Arrays.asList("nvproposal");
                    break;
                }
                case "批单修改库": {
                    owners = Arrays.asList("nvendorsement");
                    break;
                }
            }
        }
        if ("old-non-auto".equals(systemFlag)) {
            if ("pro".equals(environment)) {
                switch (database) {
                    case "保单库": {
                        owners = Arrays.asList("sunshine");
                        break;
                    }
                    case "投保单库": {
                        owners = Arrays.asList("prpins");
                        break;
                    }
                }

            }
            if ("dev".equals(environment)) {
                switch (database) {
                    case "保单库": {
                        owners = Arrays.asList("sinosoft", "stageapp");
                        break;
                    }
                    case "投保单库": {
                        owners = Arrays.asList("basecode");
                        break;
                    }

                }
            }
        }

        if ("platform".equals(systemFlag)) {
            switch (database) {
                case "平台库": {
                    owners = Arrays.asList("basecode", "platform");
                    break;
                }
            }
        }
        return owners;
    }

    public static String getDataSource(String environment, String database, String systemFlag) {
        String flag = "";
        if ("new-non-auto".equals(systemFlag)) {
            switch (database) {
                case "保单库": {
                    flag = "nvpolicy";
                    break;
                }
                case "投保单库": {
                    flag = "nvproposal";
                    break;
                }
                case "批单修改库": {
                    flag = "nvendorsement";
                    break;
                }
            }
        }
        if ("old-non-auto".equals(systemFlag)) {
            switch (database) {
                case "保单库": {
                    flag = "sunshine";
                    break;
                }
                case "投保单库": {
                    flag = "prpins";
                    break;
                }

            }
        }

        if ("platform".equals(systemFlag)) {
            switch (database) {
                case "平台库": {
                    flag = "platform";
                    break;
                }
            }
        }
        return environment + "_" + systemFlag + "_" + flag;
    }

    public static List<DatabaseTableStructureEntity> getDataStructure(String environment, String database, String systemFlag) throws Exception {

        NewJDBCUtil jdbcUtil = new NewJDBCUtil(getDataSource(environment, database, systemFlag));
        List<String> ownerList = getOwner(environment, database, systemFlag);
        String owners = "";
        for (String owner : ownerList) {
            owners = owners + "'" + owner + "',";
        }
        if (owners.contains(",")) {
            owners = owners.substring(0, owners.length() - 1);
            owners = owners.toUpperCase();
        }

        String sql = "select '" + database + "' as \"databaseName\",'" + environment + "' as \"environment\",'" + systemFlag + "' as \"systemflag\"," +
                "       a.table_name as \"tableName\",\n" +
                "       a.column_id as \"columnId\",\n" +
                "       a.column_name as \"columnName\",\n" +
                "       (case\n" +
                "         when a.data_type = 'VARCHAR2' then\n" +
                "          a.data_type || '(' || a.data_length || ')'\n" +
                "         when a.data_type = 'NUMBER' and a.data_precision is not null then\n" +
                "          a.data_type || '(' || a.data_precision || ',' || a.data_scale || ')'\n" +
                "         when a.data_type = 'NUMBER' and a.data_precision is null and\n" +
                "              a.data_scale is null then\n" +
                "          a.data_type\n" +
                "         when a.data_type = 'NUMBER' and a.data_precision is null and\n" +
                "              a.data_scale = 0 then\n" +
                "          'INTEGER'\n" +
                "         when a.data_type = 'DATE' then\n" +
                "          'DATE'\n" +
                "         when a.data_type = 'CHAR' then\n" +
                "          a.data_type || '(' || a.data_length || ')'\n" +
                "         when a.data_type = 'TIMESTAMP(6)' then\n" +
                "          'TIMESTAMP(6)'\n" +
                "         when a.data_type = 'NVARCHAR2' then\n" +
                "          a.data_type || '(' || a.data_length / 2 || ')'\n" +
                "         when a.data_type = 'CLOB' then\n" +
                "          'CLOB'\n" +
                "         when a.data_type = 'BLOB' then\n" +
                "          'BLOB'\n" +
                "         when a.data_type = 'BINARY_FLOAT' then\n" +
                "          'BINARY_FLOAT'\n" +
                "         when a.data_type = 'XMLTYPE' then\n" +
                "          'XMLTYPE'\n" +
                "         when a.data_type = 'LONG' then\n" +
                "          'LONG'\n" +
                "         when a.data_type = 'UROWID' then\n" +
                "          a.data_type || '(' || a.data_length || ')'\n" +
                "         when a.data_type = 'LONG RAW' then\n" +
                "          'LONG RAW'\n" +
                "       end) as \"columnType\",\n" +
                "   (select 'pk' from  all_constraints con,all_cons_columns col where  con.constraint_name=col.constraint_name and con.constraint_type='P' and a.TABLE_NAME=con.TABLE_NAME and a.COLUMN_NAME=col.COLUMN_NAME and a.OWNER=col.OWNER and col.OWNER=con.OWNER ) as \"keyType\",\n" +
                "       a.nullable as \"nullAble\",\n" +
                "       a.data_default as \"dataDefault\",\n" +
                "       b.comments as \"comments\" \n" +
                "  from all_tab_columns a, all_col_comments b, all_tables  c\n" +
                " where a.table_name = b.table_name\n" +
                "   and a.table_name = c.table_name\n" +
                "   and a.column_name = b.column_name\n" +
                " and c.owner in (" + owners + ")" +
                " order by 1, 2, 3 ";
        List<DatabaseTableStructureEntity> list = jdbcUtil.select(sql, DatabaseTableStructureEntity.class);
        jdbcUtil.release();
        return list;
    }

    public static List<DatabaseTableStructureEntity> getDataStructureByExcel(File dirFile,String systemFlag) throws IOException {
        List<DatabaseTableStructureEntity> list = new ArrayList<>();
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".xls")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            for (File file : files) {
                InputStream inputStream = new FileInputStream(file);
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
                List<Map> mapList = POIUtils.readExcel(hssfWorkbook, "SQL Results");
                mapList.remove(0);  //去掉第一条表头
                for (Map<String, String> map : mapList) {
                    DatabaseTableStructureEntity databaseTableStructureEntity = new DatabaseTableStructureEntity();
                    databaseTableStructureEntity.environment = map.get("environment");
                    databaseTableStructureEntity.databaseName = map.get("databasename");
                    databaseTableStructureEntity.tableName = map.get("tableName");
                    databaseTableStructureEntity.owner = map.get("owner");
                    databaseTableStructureEntity.columnId = map.get("columnId");
                    databaseTableStructureEntity.columnName = map.get("columnName");
                    databaseTableStructureEntity.columnType = map.get("columnType");
                    databaseTableStructureEntity.nullAble = map.get("nullAble");
                    databaseTableStructureEntity.dataDefault = map.get("dataDefault");
                    databaseTableStructureEntity.comments = map.get("comments");
                    databaseTableStructureEntity.systemflag=systemFlag;
                    list.add(databaseTableStructureEntity);
                }

            }
        }
        return list;
    }


    public static List<String> readFileToSqlList(File dirFile, String database) throws IOException {
        List<String> sqlList = new ArrayList<>();
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.equals(database + ".sql")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            for (File file : files) {
                sqlList.addAll(JDBCUtils.batchSqlFile(file));
            }
        }
        return sqlList;
    }

/*    private void createXLSXFile(String filePath) throws IOException, WriteException {

        //创建工作薄
        WritableWorkbook workbook = Workbook.createWorkbook(new File(filePath));
//创建工作表
// 保单库/产品工厂库/投保单库/批单修改库/统一工作台库/汇总库

        String[] databaseNames = {"投保单库", "保单库", "产品工厂库", "批单修改库", "统一工作台库", "汇总库"};
        //   String[] databaseNames = {"保单库", "产品工厂库"};

        for (int j=0; j<databaseNames.length;j++) {

            List<Map> maps = null;
*//*            try {
                maps = getDataStructure("stage", databaseNames[j]);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                continue;
            }*//*
            if (maps != null) {

                WritableSheet sheet = workbook.createSheet(databaseNames[j], j+1);
                sheet.addCell(new Label(0, 0, "编号"));
                sheet.addCell(new Label(1, 0, "OWNER"));
                sheet.addCell(new Label(2, 0, "TABLE_NAME"));
                sheet.addCell(new Label(3, 0, "COLUMN_ID"));
                sheet.addCell(new Label(4, 0, "COLUMN_NAME"));
                sheet.addCell(new Label(5, 0, "COLUMN_TYPE"));
                sheet.addCell(new Label(6, 0, "NULLABLE"));
                sheet.addCell(new Label(7, 0, "DATA_DEFAULT"));
                sheet.addCell(new Label(8, 0, "COMMENTS"));

//把上面缓存中内容写到文件中去  2表示从第几行开始标题除外
                for (int i = 0; i < maps.size(); i++) {
                    sheet.addCell(new Number(0, i + 1, i + 1));


                    String OWNER = (String) maps.get(i).get("OWNER");
                    if (OWNER != null && !"null".equals(OWNER)) {
                        sheet.addCell(new Label(1, i + 1, OWNER));
                    }
                    String TABLE_NAME = (String) maps.get(i).get("TABLE_NAME");
                    if (TABLE_NAME != null && !"null".equals(TABLE_NAME)) {
                        sheet.addCell(new Label(2, i + 1, TABLE_NAME));
                    }
                    String COLUMN_ID = (String) maps.get(i).get("COLUMN_ID");
                    if (COLUMN_ID != null && !"null".equals(COLUMN_ID)) {
                        sheet.addCell(new Label(3, i + 1, COLUMN_ID));
                    }
                    String COLUMN_NAME = (String) maps.get(i).get("COLUMN_NAME");
                    if (COLUMN_NAME != null && !"null".equals(COLUMN_NAME)) {
                        sheet.addCell(new Label(4, i + 1, COLUMN_NAME));
                    }
                    String COLUMN_TYPE = (String) maps.get(i).get("COLUMN_TYPE");
                    if (COLUMN_TYPE != null && !"null".equals(COLUMN_TYPE)) {
                        sheet.addCell(new Label(5, i + 1, COLUMN_TYPE));
                    }
                    String NULLABLE = (String) maps.get(i).get("NULLABLE");
                    if (NULLABLE != null && !"null".equals(NULLABLE)) {
                        sheet.addCell(new Label(6, i + 1, NULLABLE));
                    }
                    String DATA_DEFAULT = (String) maps.get(i).get("DATA_DEFAULT");
                    if (DATA_DEFAULT != null && !"null".equals(DATA_DEFAULT)) {
                        sheet.addCell(new Label(7, i + 1, DATA_DEFAULT));
                    }
                    String COMMENTS = (String) maps.get(i).get("COMMENTS");
                    if (COMMENTS != null && !"null".equals(COMMENTS)) {
                        sheet.addCell(new Label(8, i + 1, COMMENTS));
                    }
                }
            }
        }
        workbook.write();
        workbook.close();

    }*/


    public static void main(String args[]) {
        GetDataStructure getDataStructure = new GetDataStructure();
/*        try {
            getDataStructure.createXLSXFile("D:\\work\\pyproject\\DatabaseStructureCompare\\测试表结构.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }*/
    }

}
