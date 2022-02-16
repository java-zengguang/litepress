package com.zg.sinosig.driver.check;

import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;

import java.sql.SQLException;
import java.util.*;

public class VerificationCheck implements CheckSQL {
    private NewJDBCUtil jdbcUtil;
    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures,reloadDataBaseTableStructures");
    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog"));

    public VerificationCheck() {
        this.jdbcUtil = new NewJDBCUtil("test");
    }

    public VerificationCheck(String dataSource) {
        this.jdbcUtil = new NewJDBCUtil(dataSource);
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

    //读取sql脚本里的表名
    private Set<String> getTableFromSQL(String sourceSQL,String sqlType) {
        Set<String> tableNameSet = new HashSet<>();
        if("DDL".equals(sqlType)) {
            List<String> sqlList = formatSQL(sourceSQL);
            for (String sql : sqlList) {
                List<String> list = Arrays.asList(sql.trim().split("\\s+"));
                String opreate = list.get(0);
                String opreateObject = list.get(1);
                String tableName = list.get(2);
                if (opreate.trim().contains("alter") && "table".equals(opreateObject.trim())) {
                    tableNameSet.add(tableName);
                }
            }
        }
        if("DML".equals(sqlType)) {
            List<String> sqlList = formatSQL(sourceSQL);
            for (String sql : sqlList) {
                List<String> list = Arrays.asList(sql.trim().split("\\s+"));
                String opreate = list.get(0);

                if (opreate.trim().contains("update")) {
                    tableNameSet.add(list.get(1));
                }
                if (opreate.trim().contains("insert")) {
                    tableNameSet.add(list.get(2));
                }

            }
        }

        return tableNameSet;
    }


    private List<String> createTableSQL(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws Exception {
        List<String> list = new ArrayList<>();

        Set<String> tableNames = getTableFromSQL(sinoSigSQLLogEntity.getBasesql(),sinoSigSQLLogEntity.sqltype);
        for (String tableName : tableNames) {
            String sql = "";
            List<DatabaseTableStructureEntity> databaseTableStructureEntities = strcutureService.getTableStructure(sinoSigSQLLogEntity.environment, sinoSigSQLLogEntity.databasename, tableName,sinoSigSQLLogEntity.systemflag);
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
            }else{
                throw new Exception("未找到待加载表名"+tableName);
            }
        }
        return list;
    }


    private boolean initTestEnvironment(List<SinoSigSQLLogEntity> list) {


        try {
            List<SinoSigSQLLogEntity> testEntitys = new ArrayList<>();
            //解析脚本，获取操作的表，检验套表规范

            List<String> initSQLList = new ArrayList();
            //初始化环境
            //初始化模拟环境数据库，创建三个模拟库
            initSQLList.add(" create SCHEMA nvpolicy ");
            initSQLList.add(" create SCHEMA nvproposal ");
            initSQLList.add(" create SCHEMA nvendorsement ");
            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                //模拟环境复制对象
                SinoSigSQLLogEntity testEntity = (SinoSigSQLLogEntity) sinoSigSQLLogEntity.clone();
                testEntity.environment = "test";
                testEntitys.add(testEntity);
                //初始化新建表
                List<String> sqlList = getInitSQL(sinoSigSQLLogEntity);
                initSQLList.addAll(sqlList);

                if("DDL".equals(sinoSigSQLLogEntity.sqltype)) {
                    if ("增加字段".equals(sinoSigSQLLogEntity.sqlpurpose) || "拉长字段".equals(sinoSigSQLLogEntity.sqlpurpose)) {

                    }
                }
                if("DML".equals(sinoSigSQLLogEntity.sqltype)) {
                    if ("插入数据".equals(sinoSigSQLLogEntity.sqlpurpose) || "更新数据".equals(sinoSigSQLLogEntity.sqlpurpose)) {

                    }
                }

            }

            jdbcUtil.batchSql(initSQLList, true);

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }


    public List<SinoSigSQLLogEntity> checkDDL(List<SinoSigSQLLogEntity> list) throws Exception {
        //将所有脚本在模拟环境运行后，可运行检查，做逻辑性检查，套表检查，按批次做

        String errorMessage = "";
        String stageFlag = "3";



        if (!initTestEnvironment(list)) {
            errorMessage = "环境初始化失败";
            stageFlag = "-1";
        } else {

            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                List<String> executeSQLList = formatSQL(sinoSigSQLLogEntity.basesql);
                try {
                    jdbcUtil.batchSql(executeSQLList, true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    stageFlag = "-1";
                    String message = e.getMessage().replaceAll("'", "");
                    if (message.length() > 100) {
                        message = message.substring(0, 100);
                    }
                    sinoSigSQLLogEntity.setErrormassage("脚本执行错误" + message);
                    continue;
                }
            }


            //加载表结构到本地Mysql

            String sql = "select\n" +
                    "\t(case\n" +
                    "\t\twhen a.TABLE_SCHEMA = 'NVPROPOSAL' then '投保单库'\n" +
                    "\t\twhen a.TABLE_SCHEMA = 'NVENDORSEMENT' then '批单修改库'\n" +
                    "\t\twhen a.TABLE_SCHEMA = 'NVPOLICY' then '保单库'\n" +
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
                    "\tINFORMATION_SCHEMA.COLUMNS a" +
                    " where a.TABLE_SCHEMA  in ('NVENDORSEMENT','NVPROPOSAL','NVPOLICY')  ";
            List<DatabaseTableStructureEntity> list1 = jdbcUtil.select(sql, DatabaseTableStructureEntity.class);
            strcutureService.reloadDataBaseTableStructures(list1,"test", "new-non-auto");

        }


        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            sinoSigSQLLogEntity.setErrormassage(errorMessage);
            sinoSigSQLLogEntity.executestate = stageFlag;
        }
        //回滚环境，删除数据库
        List<String> initSQLList = new ArrayList();
        //初始化环境
        //初始化模拟环境数据库，创建三个模拟库 强制删除
        initSQLList.add(" drop SCHEMA nvpolicy CASCADE");
        initSQLList.add(" drop SCHEMA nvproposal CASCADE");
        initSQLList.add(" drop SCHEMA nvendorsement CASCADE");
        jdbcUtil.batchSql(initSQLList, true);

        //最后提交，H2数据库消失
        jdbcUtil.commit();
        jdbcUtil.release();
        return list;
    }


    private void checkDML(List<SinoSigSQLLogEntity> list) {

        String errorMessage = "";
        String stageFlag = "3";

        if (!initTestEnvironment(list)) {
            errorMessage = "环境初始化失败";
            stageFlag = "-1";
        } else {

            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                List<String> executeSQLList = formatSQL(sinoSigSQLLogEntity.basesql);
                try {
                    jdbcUtil.batchSql(executeSQLList, true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    stageFlag = "-1";
                    String message = e.getMessage().replaceAll("'", "");
                    if (message.length() > 100) {
                        message = message.substring(0, 100);
                    }
                    sinoSigSQLLogEntity.setErrormassage("脚本执行错误" + message);
                    continue;
                }
            }
        }
        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            sinoSigSQLLogEntity.setErrormassage(errorMessage);
            sinoSigSQLLogEntity.executestate = stageFlag;
        }
    }

    @Override
    public List<SinoSigSQLLogEntity> checkSQL(List<SinoSigSQLLogEntity> list) throws Exception {
        //分组+单个校验
        List<SinoSigSQLLogEntity> ddlList=new ArrayList<>();
        List<SinoSigSQLLogEntity> dmlList=new ArrayList<>();

        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            //初步校验失败的不进入分组校验
           if("3".equals(sinoSigSQLLogEntity.executestate)){
                //分组
                if ("DDL".equals(sinoSigSQLLogEntity.getSqltype())) {
                    ddlList.add(sinoSigSQLLogEntity);
                } else if("DML".equals(sinoSigSQLLogEntity.getSqltype())){
                    dmlList.add(sinoSigSQLLogEntity);
                }
            }
        }
        if(ddlList!=null&&ddlList.size()>0) {
            checkDDL(ddlList);
        }

        if(dmlList!=null&&dmlList.size()>0) {
            checkDML(dmlList);
        }

        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            sinoSigSQLLogService.updateStateSinoSingSQLLog(sinoSigSQLLogEntity);
        }

        return list;
    }



}
