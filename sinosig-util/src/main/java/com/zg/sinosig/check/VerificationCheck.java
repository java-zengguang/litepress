package com.zg.sinosig.check;

import com.zg.handler.ProxyUtils;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;

import java.sql.SQLException;
import java.util.*;

public class VerificationCheck {
    private NewJDBCUtil jdbcUtil;
    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures,reloadDataBaseTableStructures");


    public VerificationCheck(String dataSource) {
        this.jdbcUtil = new NewJDBCUtil("test");
    }

    private List<String> getInitSQL(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws Exception {

        List<String> list = createTableSQL(sinoSigSQLLogEntity);
        return list;
    }


    private List<String> formatSQL(String sourceSQL) {
        //清洗脚本  去掉注释
        String[] lines = sourceSQL.split("\n");
        String exeSQL = "";
        for (String line : lines) {
            if (line.contains("--")) {
                line = "";
            }
            exeSQL = exeSQL + line;
        }

        sourceSQL = exeSQL.toLowerCase();
        List<String> sqlList = new ArrayList<>();

        if (sourceSQL.contains(";")) {
            String str[] = sourceSQL.split(";");
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

    private Set<String> getTableFromSQL(String sourceSQL) {
        Set<String> tableNameSet = new HashSet<>();
        List<String> sqlList = formatSQL(sourceSQL);

        for (String sql : sqlList) {
            List<String> list = Arrays.asList(sql.trim().split("\\s+"));
            String opreate = list.get(0);
            String opreateObject = list.get(1);
            String tableName = list.get(2);
      /*      if (tableName.contains(".")) {
                tableName = tableName.substring(tableName.indexOf(".") + 1, tableName.length());
            }*/
            if (opreate.trim().contains("alter") && "table".equals(opreateObject.trim())) {
                tableNameSet.add(tableName);
            }
        }

        return tableNameSet;
    }


    private List<String> createTableSQL(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws Exception {
        List<String> list = new ArrayList<>();

        Set<String> tableNames = getTableFromSQL(sinoSigSQLLogEntity.getBasesql());
        for (String tableName : tableNames) {
            String sql = "";
            List<DatabaseTableStructureEntity> databaseTableStructureEntities = strcutureService.getTableStructure(sinoSigSQLLogEntity.environment, sinoSigSQLLogEntity.databasename, tableName);
            if (databaseTableStructureEntities != null && databaseTableStructureEntities.size() > 0) {


                String columns = "";
                for (DatabaseTableStructureEntity databaseTableStructureEntity : databaseTableStructureEntities) {
                    columns = columns + "  \"" + databaseTableStructureEntity.columnName + "\"   " + databaseTableStructureEntity.columnType;
                    if ("N".equals(databaseTableStructureEntity.nullAble)) {
                        columns = columns + "  not null ,";
                    } else {
                        columns = columns + "  ,";
                    }
                }
                columns = columns.substring(0, columns.length() - 1);
                sql = "create table " + tableName + " ( \r\n" + columns + " \r\n);";
                list.add(sql);
            }
        }
        return list;
    }




    private boolean initTestEnvironment(List<SinoSigSQLLogEntity> list){


        try {
            List<SinoSigSQLLogEntity> testEntitys = new ArrayList<>();
            //解析脚本，获取操作的表，检验套表规范

            List<String> initSQLList = new ArrayList();
            //初始化环境
            //初始化模拟环境数据库，创建三个模拟库
            initSQLList.add(" create SCHEMA nvpolicy ");
            initSQLList.add(" create SCHEMA nvproposal ");
            initSQLList.add(" create SCHEMA nvendorsement ");
            //初始化套表关系
/*            initSQLList.add(" CREATE TABLE `ldcode` (\n" +
                    "  `codetype` varchar(20) NOT NULL,\n" +
                    "  `codecode` varchar(100) NOT NULL,\n" +
                    "  `codecname` varchar(600) NOT NULL,\n" +
                    "  `flag` varchar(100) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`codetype`,`codecode`,`codecname`)\n" +
                    ") ");*/

            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                //模拟环境复制对象
                SinoSigSQLLogEntity testEntity = (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
                testEntity.environment = "test";
                testEntitys.add(testEntity);
                //拉长字段和增加字段，需要在目标环境建表
                if ("增加字段".equals(sinoSigSQLLogEntity.sqlpurpose) || "拉长字段".equals(sinoSigSQLLogEntity.sqlpurpose)) {
                    List<String> sqlList = getInitSQL(sinoSigSQLLogEntity);
                    initSQLList.addAll(sqlList);
                }

            }

            jdbcUtil.batchSql(initSQLList,true);

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }


    public List<SinoSigSQLLogEntity> checkDDL(List<SinoSigSQLLogEntity> list) throws Exception {
        //将所有脚本在模拟环境运行后，可运行检查，做逻辑性检查，套表检查，按批次做

            String errorMessage="";
            String stageFlag="3";

            if(!initTestEnvironment(list)){
                errorMessage="环境初始化失败";
                stageFlag="-1";
            }else{

                for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                    List<String> executeSQLList = formatSQL(sinoSigSQLLogEntity.basesql);
                    try {
                        jdbcUtil.batchSql(executeSQLList, true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        stageFlag="-1";
                        String message=e.getMessage().replaceAll("'","");
                        if (message.length()>100){
                            message=message.substring(0,100);
                        }
                        sinoSigSQLLogEntity.setErrormassage("脚本执行错误"+message);
                        continue;
                    }
                }

                //加载表结构到本地Mysql

                String sql="select\n" +
                        "\t(case\n" +
                        "\t\twhen a.TABLE_SCHEMA = 'NVPROPOSAL' then '投保单库'\n" +
                        "\t\twhen a.TABLE_SCHEMA = 'NVENDORSEMENT' then '批单修改库'\n" +
                        "\t\twhen a.TABLE_SCHEMA = 'NVPOLICY' then ' 保单库'\n" +
                        "\t\telse a.TABLE_SCHEMA end ) as \"databaseName\",\n" +
                        "\ta.TABLE_SCHEMA as \"owner\",\n" +
                        "\t'test' as \"environment\" ,\n" +
                        "\ta.TABLE_NAME as \"tableName\" ,\n" +
                        "\t(a.data_type||CHARACTER_MAXIMUM_LENGTH||NUMERIC_PRECISION||NUMERIC_SCALE) as \"columnType\",\n" +
                        "\ta.COLUMN_NAME as \"columnName\" ,\n" +
                        "\t'' as \"nullAble\",\n" +
                        "\t'' as \"dataDefault\",\n" +
                        "\t'' as \"comments\"\n" +
                        "from\n" +
                        "\tINFORMATION_SCHEMA.COLUMNS a"+
                        " where a.TABLE_SCHEMA  in ('NVENDORSEMENT','NVPROPOSAL','NVPOLICY')  ";
                List<DatabaseTableStructureEntity> list1= jdbcUtil.select(sql,DatabaseTableStructureEntity.class);
                strcutureService.reloadDataBaseTableStructures(list1,"test");
                List<Map> checkResult=new ArrayList<>();

                checkResult=strcutureService.getCompareResult("test","CT");
                if(checkResult!=null&&checkResult.size()>1){//里面自带表头
                    errorMessage=errorMessage+"当前批次套表字段类型检查不通过;";
                    errorMessage=errorMessage+checkResult;
                    stageFlag="-1";
                }
                checkResult=strcutureService.getCompareResult("test","CTC");
                if(checkResult!=null&&checkResult.size()>1){//自带表头，所以>1
                    errorMessage=errorMessage+"当前批次套表缺少字段检查不通过;";
                    errorMessage=errorMessage+checkResult;
                    stageFlag="-1";
                }

/*        checkResult=strcutureService.getCompareResult("test","CTT");
        if(checkResult!=null&&checkResult.size()>0){
            errorMessage=errorMessage+"套表缺失检查不通过;";
        }*/

            }


            for(SinoSigSQLLogEntity sinoSigSQLLogEntity :list){
                sinoSigSQLLogEntity.setErrormassage(errorMessage);
                sinoSigSQLLogEntity.executestate=stageFlag;
            }
            //回滚环境，删除数据库
            List<String> initSQLList = new ArrayList();
            //初始化环境
            //初始化模拟环境数据库，创建三个模拟库 强制删除
            initSQLList.add(" drop SCHEMA nvpolicy CASCADE");
            initSQLList.add(" drop SCHEMA nvproposal CASCADE");
            initSQLList.add(" drop SCHEMA nvendorsement CASCADE");
            jdbcUtil.batchSql(initSQLList,true);

            //最后提交，H2数据库消失
            jdbcUtil.commit();
            jdbcUtil.release();
            return list;
    }

}
