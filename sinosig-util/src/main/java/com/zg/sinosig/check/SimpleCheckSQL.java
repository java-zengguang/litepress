package com.zg.sinosig.check;

import com.zg.database.util.JDBCUtils;
import com.zg.handler.CommitInterfaceHandler;
import com.zg.handler.ProxyUtils;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;
import com.zg.webdemo.service.ldcode.LDCodeService;
import com.zg.webdemo.service.ldcode.LDCodeServiceImpl;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogService;
import com.zg.webdemo.service.sinosigsqllog.SinoSigSQLLogServiceImpl;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class SimpleCheckSQL implements CheckSQL {

    //private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyClass(new SinoSigSQLLogServiceImpl(), "insertSinoSingSQLLog,updateStateSinoSingSQLLog");
    private SinoSigSQLLogService sinoSigSQLLogService = (SinoSigSQLLogService) ProxyUtils.getProxyInterface(SinoSigSQLLogServiceImpl.class, new CommitInterfaceHandler(new SinoSigSQLLogServiceImpl(),"insertSinoSingSQLLog,updateStateSinoSingSQLLog"));

    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures,reloadDataBaseTableStructures");

    LDCodeService ldCodeService= (LDCodeService) ProxyUtils.getProxyClass(new LDCodeServiceImpl(),"reLoadPRPTable");

    static Logger logger = Logger.getLogger(SQLUtils.class.getName());


    public boolean checkSQLScript(SinoSigSQLLogEntity sinoSigSQLLogEntity) {

        String vsql = sinoSigSQLLogEntity.basesql;
        if (vsql == null || vsql.equals("")) {
            sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本不能为空！");
            return false;
        }

        if (true) {

            String sql = vsql.toLowerCase().trim() + "";

            if (sql == null || sql.equals("")) {
                sinoSigSQLLogEntity.setErrormassage("  检查未通过:不能使用外键，建议使用业务逻辑判断来代替外键!!");
                return false;
            }

            //非阻断
        if (sql.contains("database") || sql.contains("revoke") || sql.contains("flush") || sql.contains("truncate") || sql.contains("rename") || sql.contains("view") || sql.contains("function") || sql.contains("procedure") || sql.contains("drop")) {
            sinoSigSQLLogEntity.setErrormassage("请注意:包含数据库敏感关键字(database,revoke,flush,truncate,rename,view,function,procedure,drop)中的一个或多个，为防止误操作，请人工检查!!");
         //   return false;
        }

            if (sql.contains("sequence")) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:创建序列请注意!!");
                return false;
            }

            if (sql.contains("create table") && !(sql.contains("primary") && sql.contains("key"))) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:新创建表没有主键，表必须有主键!!");
                return false;
            }


            if (!sql.endsWith(";")) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本必须有结束符!!");
                return false;
            }

            if (true) {
                String[] sqlArray = sql.split(";");
                for (int i = 0; i < sqlArray.length - 1; i++) {
                    if (sqlArray[i].trim().length() < 1) {
                        sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本分割检查有误，请检查是否多了结算符!!");

                    }
                }
            }
        }

        if (true) {
            String xsql = vsql.toLowerCase().trim() + "";

            List<String> sqlList = new ArrayList();
            if (xsql.contains(";")) {
                String str[] = xsql.split(";");
                for (String s : str) {
                    s = s.trim();
                    if (s != null && !"".equals(s)) {
                        sqlList.add(s);
                    }
                }
            } else {
                sqlList.add(xsql);
            }

            for (String sql : sqlList) {
                sql = sql.toLowerCase();
                if (sql == null || "".equals(sql)) {
                    continue;
                }
                List<String> list = Arrays.asList(sql.split("\\s+"));
                if (list.contains("create") && !list.contains("synonym") || list.contains("alter")) {
                    String opreate = list.get(0);
                    String opreateObject = list.get(1);
                    String tableName = list.get(2);
                    String exeOpreate = list.get(3);
                    String columnName = list.get(4);
                    String columnType = list.get(5);
                    //去掉属主
                    if (opreateObject.contains(".")) {

                    }
                    if (tableName.contains(".")) {
                        tableName = tableName.substring(tableName.indexOf(".") + 1, tableName.length());
                    }
                    if ("table".equals(opreateObject)) {
                        String flag = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('pro') and d.tablename ='" + tableName + "'");
                        if ("".equals(flag) && "alter".equals(opreate) && !sql.contains("primary")) {
                            sinoSigSQLLogEntity.setErrormassage("检查未通过:生产没有这个表 " + "tablename:" + tableName);
                            return false;
                        }
                        if ("1".equals(flag) && "create".equals(opreate)) {
                            sinoSigSQLLogEntity.setErrormassage("检查未通过:生产已经有这个表了 " + "tablename:" + tableName);
                            return false;
                        }

                        if (("alter").equals(opreate)) {
                            if (("drop").equals(exeOpreate)) {
                                sinoSigSQLLogEntity.setErrormassage("检查未通过:单个脚本不允许 drop ");
                                return false;
                            }
                            if (("rename").equals(exeOpreate)) {
                                sinoSigSQLLogEntity.setErrormassage("检查未通过:单个脚本不允许 rename ");
                                return false;
                            }
                            if ("add".equals(exeOpreate)) {
                                String flag1 = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('pro') and d.databasename='"+ sinoSigSQLLogEntity.databasename+"' and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "' ");
                                if ("1".equals(flag1)) {
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:生产已有此字段" + "tablename:" + tableName + " columnname:" + columnName);
                                    return false;
                                }
                                flag1 = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('old','old_dev') and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "'  and d.columntype <> '" + columnType + "' ");
                                if ("1".equals(flag1)) {
                                    logger.info("检查未通过:老核心已有此字段，且字段类型或长度不一致" + "tablename:" + tableName + " columnname:" + columnName);
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:老核心已有此字段，且字段类型或长度不一致" + "tablename:" + tableName + " columnname:" + columnName);
                                   // return false;
                                }

                            }
                            if (("modify").equals(exeOpreate)) {

                                String oldColumnType = JDBCUtils.getOneValue("select d.columntype from databasetablestructure d where d.environment  in ('pro') and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "' ");
                                if ("".equals(oldColumnType)) {
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:生产不存在此字段" + "tablename:" + tableName + " columnname:" + columnName);
                                    return false;
                                }
                                if (!compareColumnType(oldColumnType, columnType, sinoSigSQLLogEntity)) {
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:字段类型校验失败" + "tablename:" + tableName + " columnname:" + columnName);
                                    return false;
                                }
                            }

                        }
                    }

                }

            }
        }

        return true;
    }

    private boolean compareColumnType(String oldColumnType, String columnType, SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        oldColumnType = oldColumnType.toUpperCase();
        columnType = columnType.toUpperCase();
        String oldColumnFlag = oldColumnType.substring(0, (oldColumnType.indexOf("(") != -1) ? oldColumnType.indexOf("(") : oldColumnType.length());
        String[] oldColumnLengthStrs = oldColumnType.replace(oldColumnFlag, "").replace("(", "").replace(")", "").split(",");
        String columnFlag = columnType.substring(0, (columnType.indexOf("(") != -1) ? columnType.indexOf("(") : columnType.length());
        String[] columnLengthStrs = columnType.replace(columnFlag, "").replace("(", "").replace(")", "").split(",");

        if (!oldColumnFlag.equals(columnFlag)) {
            sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本中涉及字段类型变更！");
            return false;
        }


        if (oldColumnLengthStrs != null && columnLengthStrs != null) {

            if (oldColumnLengthStrs.length != columnLengthStrs.length) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本字段类型涉及精度变更！");
                return false;
            }

            for (int i = 0; i < columnLengthStrs.length; i++) {
                Integer columnLength = Integer.parseInt("".equals(columnLengthStrs[i]) ? "-1" : columnLengthStrs[i]);
                Integer oldColumnLength = Integer.parseInt("".equals(oldColumnLengthStrs[i]) ? "-1" : oldColumnLengthStrs[i]);
                if (columnLength < oldColumnLength) {
                    sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本字段长度不允许改小！");
                    return false;
                }

            }
        }

        return true;

    }


    //检查一个对象的脚本，主要做合法性检查
    private boolean checkOneEntitySQL(SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        if (checkSQLScript(sinoSigSQLLogEntity)) {
            sinoSigSQLLogEntity.executestate = "3";
            return true;
        } else {
            sinoSigSQLLogEntity.executestate = "-1";
            return false;
        }

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

        NewJDBCUtil jdbcUtil = new NewJDBCUtil("test");
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

            jdbcUtil.batchSql(initSQLList, false);
            jdbcUtil.commit();

        } catch (Exception exception) {
            exception.printStackTrace();
            jdbcUtil.release();
            return false;
        }

        return true;
    }

    //将所有脚本在模拟环境运行后，可运行检查，做逻辑性检查，套表检查，按批次做
    private boolean verificationCheck(List<SinoSigSQLLogEntity> list) throws Exception {
        String errorMessage="";
        String stageFlag="3";

        if(!initTestEnvironment(list)){
             errorMessage="环境初始化失败";
             stageFlag="-1";
        }else{
            NewJDBCUtil jdbcUtil=new NewJDBCUtil("test");
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
            jdbcUtil.commit(); //数据库在DB_CLOSE_DELAY=-1  ，程序停止时消失
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
            jdbcUtil=new NewJDBCUtil("test");
            List<DatabaseTableStructureEntity> list1= jdbcUtil.select(sql,DatabaseTableStructureEntity.class);
            jdbcUtil.release();

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
        NewJDBCUtil jdbcUtil=new NewJDBCUtil("test");
        //初始化环境
        //初始化模拟环境数据库，创建三个模拟库 强制删除
        initSQLList.add(" drop SCHEMA nvpolicy CASCADE");
        initSQLList.add(" drop SCHEMA nvproposal CASCADE");
        initSQLList.add(" drop SCHEMA nvendorsement CASCADE");
        jdbcUtil.batchSql(initSQLList,true);
        jdbcUtil.commit();

        return true;
    }


    @Override
    public List<SinoSigSQLLogEntity> checkSQL(List<SinoSigSQLLogEntity> list) throws Exception {
        //分组+单个校验
        Map<String, List<SinoSigSQLLogEntity>> ddlMapList = new HashMap<>();
        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            //初步校验失败的不进入分组校验
            if (checkOneEntitySQL(sinoSigSQLLogEntity) == false) {
                continue;
            }else {
                //只有ddl语句做分组校验
                if ("DDL".equals(sinoSigSQLLogEntity.sqltype.toUpperCase())) {
                    String batchNo = sinoSigSQLLogEntity.batchno;
                    List<SinoSigSQLLogEntity> sqlLogEntities = ddlMapList.get(batchNo);
                    if (sqlLogEntities == null) {
                        sqlLogEntities = new ArrayList<>();
                    }
                    sqlLogEntities.add(sinoSigSQLLogEntity);
                    ddlMapList.put(batchNo, sqlLogEntities);

                }else{

                }
            }
        }


        //分组校验
        Set<String> keySet = ddlMapList.keySet();
        for (String key : keySet) {
            List<SinoSigSQLLogEntity> sqlLogEntities = ddlMapList.get(key);
            verificationCheck(sqlLogEntities);
        }


        for(SinoSigSQLLogEntity sinoSigSQLLogEntity :list){
           // sinoSigSQLLogService.updateStateSinoSingSQLLog(sinoSingSQLLogEntity);
        }

        return list;
    }
}
