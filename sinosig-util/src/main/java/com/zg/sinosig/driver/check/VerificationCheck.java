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
import java.util.stream.Collectors;

public abstract class VerificationCheck implements CheckSQL {
    public NewJDBCUtil jdbcUtil;
    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures,reloadDataBaseTableStructures");
    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog"));

    public VerificationCheck() {
        this.jdbcUtil = new NewJDBCUtil("test");
    }

    public VerificationCheck(String dataSource) {
        this.jdbcUtil = new NewJDBCUtil(dataSource);
    }

    private List<String> getInitSQL(SinoSigSQLLogEntity sinoSigSQLLogEntity) {

        List<String> list = new ArrayList<>();
        try {
            list = createTableSQL(sinoSigSQLLogEntity);
        } catch (Exception e) {
            e.printStackTrace();
            sinoSigSQLLogEntity.executestate = "-1";
            sinoSigSQLLogEntity.errormassage = "表结构加载失败";

        }

        return list;
    }


    private List<String> splitSQL(String sourceSQL) {

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
    private Set<String> getTableFromSQL(String sourceSQL, String sqlType) {
        Set<String> tableNameSet = new HashSet<>();
        if ("DDL".equals(sqlType)) {
            List<String> sqlList = splitSQL(sourceSQL);
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
        if ("DML".equals(sqlType)) {
            List<String> sqlList = splitSQL(sourceSQL);
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

        Set<String> tableNames = getTableFromSQL(sinoSigSQLLogEntity.getBasesql(), sinoSigSQLLogEntity.sqltype);

        for (String tableName : tableNames) {
            String sql = "";
            List<DatabaseTableStructureEntity> databaseTableStructureEntities = strcutureService.getTableStructure(sinoSigSQLLogEntity.environment, sinoSigSQLLogEntity.databasename, tableName, sinoSigSQLLogEntity.systemflag);
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
            } else {
                sinoSigSQLLogEntity.executestate = "-1";
                sinoSigSQLLogEntity.errormassage = "未找到待加载表名" + tableName;
            }
        }
        return list;
    }

    private boolean executeOneTestEnvironment(SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        List<String> targetSQLList = splitSQL(sinoSigSQLLogEntity.basesql);
        try {
            jdbcUtil.batchSql(targetSQLList, true);
        } catch (SQLException e) {
            e.printStackTrace();
            String message = e.getMessage();
            if (message.length() > 100) {
                message.substring(100);
            }
            sinoSigSQLLogEntity.setErrormassage(message);
            sinoSigSQLLogEntity.executestate = "-1";
            return false;
        }
        return true;
    }

    private boolean initTestEnvironment(List<SinoSigSQLLogEntity> list) {
        //解析脚本，获取操作的表，检验套表规范
        //初始化环境，根据属主创建测试库
        Map<String, List<SinoSigSQLLogEntity>> ownerMapList = list.stream().collect(Collectors.groupingBy(SinoSigSQLLogEntity::getOwner)); //按属主分类
        Set<String> ownerSet = ownerMapList.keySet();
        for (String owner : ownerSet) {
            String flag = "3";
            String errorMassage = "";
            List<SinoSigSQLLogEntity> sinoSigSQLLogEntities = ownerMapList.get(owner);
            List<String> initSQLList = new ArrayList();
            initSQLList.add(" create SCHEMA " + owner + " ");//创建属主空间
            //去重处理
            Set<String> initSet=new HashSet<>();

            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : sinoSigSQLLogEntities) {
                if ("3".equals(flag)&&!"建表".equals(sinoSigSQLLogEntity.sqlpurpose)) {

                    //模拟环境复制对象
                    String baseSql = formatSQL(sinoSigSQLLogEntity.basesql, owner);
                    sinoSigSQLLogEntity.basesql = baseSql; //格式化脚本，并添加上属主
                    //解析获取表初始化语句
                    List<String> initTableSQLList = getInitSQL(sinoSigSQLLogEntity);//表执行脚本
                    initSet.addAll(initTableSQLList);
                } else {
                    sinoSigSQLLogEntity.setErrormassage(errorMassage);
                    sinoSigSQLLogEntity.executestate = flag;
                }
            }

            try {

                initSQLList.addAll(initSet);
                jdbcUtil.batchSql(initSQLList, true);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String formatSQL(String basesql, String owner) {
        String resultSQL = "";
        basesql = formatSQL(basesql);
        List<String> sqlList = splitSQL(basesql);
        for (String sql : sqlList) {
            String newSQL = "";
            List<String> list = Arrays.asList(sql.trim().split("\\s+"));
            String s1 = list.get(0);
            String s2 = list.get(1);
            String s3 = list.get(2);
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

            for (String s : list) {
                newSQL = newSQL + s + " ";
            }
            System.out.println(s3);
            resultSQL = resultSQL + newSQL + ";\n";
        }


        return resultSQL;
    }

    //清洗脚本  去掉注释
    private String formatSQL(String sourceSQL) {

        String[] lines = sourceSQL.split("\n");
        String exeSQL = "";
        for (String line : lines) {
            if (line.contains("--") && !line.contains(";")) {
                line = "";
            }
            exeSQL = exeSQL + line;
        }

        sourceSQL = exeSQL.toLowerCase();
        return sourceSQL;
    }


    public abstract void saveDataBaseStrucutre() throws Exception;


    public void checkDDL(List<SinoSigSQLLogEntity> list) throws Exception {
        //将所有脚本在模拟环境运行后，可运行检查，做逻辑性检查，套表检查，按批次做
        String errorMessage = "";
        String stageFlag = "3";

        if (!initTestEnvironment(list)) {
            errorMessage = "环境初始化失败";
            stageFlag = "-1";
        } else {

            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                List<String> executeSQLList = splitSQL(sinoSigSQLLogEntity.basesql);
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

        saveDataBaseStrucutre(); //ddl表结构回调

        //最后提交，H2数据库消失
        jdbcUtil.commit();
        jdbcUtil.release();
    }


    private void checkDML(List<SinoSigSQLLogEntity> list) throws SQLException {

        String errorMessage = "";
        String stageFlag = "3";

        if (!initTestEnvironment(list)) {
            errorMessage = "环境初始化失败";
            stageFlag = "-1";
        } else {

            for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
                List<String> executeSQLList = splitSQL(sinoSigSQLLogEntity.basesql);
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
        jdbcUtil.commit();
        //最后提交 关闭链接，H2数据库消失
        jdbcUtil.release();
    }

    @Override
    public List<SinoSigSQLLogEntity> checkSQL(List<SinoSigSQLLogEntity> list) throws Exception {
        //分组+单个校验
        List<SinoSigSQLLogEntity> ddlList = new ArrayList<>();
        List<SinoSigSQLLogEntity> dmlList = new ArrayList<>();

        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            //初步校验失败的不进入分组校验
            if ("3".equals(sinoSigSQLLogEntity.executestate)) {
                //分组
                if ("DDL".equals(sinoSigSQLLogEntity.getSqltype())) {
                    ddlList.add(sinoSigSQLLogEntity);
                } else if ("DML".equals(sinoSigSQLLogEntity.getSqltype())) {
                    dmlList.add(sinoSigSQLLogEntity);
                }
            }
        }
        if (ddlList != null && ddlList.size() > 0) {
            checkDDL(ddlList);
        }

        if (dmlList != null && dmlList.size() > 0) {
            checkDML(dmlList);
        }

        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            sinoSigSQLLogService.updateStateSinoSingSQLLog(sinoSigSQLLogEntity);
        }

        return list;
    }


}
