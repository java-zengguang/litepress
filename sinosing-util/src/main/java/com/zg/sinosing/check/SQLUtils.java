package com.zg.sinosing.check;

import com.zg.database.util.JDBCUtils;
import com.zg.util.sinosing.DatabaseUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SQLUtils {
    static Logger logger = Logger.getLogger(SQLUtils.class.getName());


    public static boolean checkSQLScript(String vsql) {

        if (vsql == null || vsql.equals("")) {
            System.out.println("  检查未通过:脚本不能为空！");
            return false;
        }

        String sql = vsql.toLowerCase().trim() + "";

        if (sql == null || sql.equals("")) {
            System.out.println("  检查未通过:不能使用外键，建议使用业务逻辑判断来代替外键!!");
            return false;
        }

/*        if (sql.contains("database") || sql.contains("revoke") || sql.contains("flush") || sql.contains("truncate") || sql.contains("rename") || sql.contains("view") || sql.contains("function") || sql.contains("procedure") || sql.contains("drop")) {
            System.out.println("检查未通过:包含数据库敏感关键字(database,revoke,flush,truncate,rename,view,function,procedure,drop)中的一个或多个，为防止误操作，禁止执行，有误判情况，请联系DBA!!");
            return false;
        }*/

        if (sql.contains("sequence")) {
            System.out.println("检查未通过:创建序列请注意!!");
            return false;
        }

        if (sql.contains("create table") && !(sql.contains("primary") && sql.contains("key"))) {
            System.out.println("检查未通过:新创建表没有主键，表必须有主键!!");
            return false;
        }

/*
        if (sql.contains("delete") && !sql.contains("where")) {
            System.out.println("检查未通过:不能全表删除!!");
            return false;
        }
*/


        if(!sql.endsWith(";")){
            System.out.println("检查未通过:脚本必须有结束符!!");
            return false;
        }

        if(true){
            String[] sqlArray=sql.split(";");
            for(int i=0;i<sqlArray.length-1;i++){
                if (sqlArray[i].trim().length()<1){
                    System.out.println("检查未通过:脚本分割检查有误，请检查是否多了结算符!!");

                }
            }
        }


        return true;
    }

    public static boolean checkSQLList(List<String> sqlList) {
        for (String sql : sqlList) {
            sql = sql.toLowerCase();
            if (sql == null || "".equals(sql)) {
                continue;
            }
            List<String> list = Arrays.asList(sql.split("\\s+"));
            if (list.contains("create")&&!list.contains("synonym") || list.contains("alter") ) {
                String opreate = list.get(0);
                String opreateObject = list.get(1);
                String tableName = list.get(2);
                String exeOpreate = list.get(3);
                String columnName = list.get(4);
                String columnType = list.get(5);
                //去掉属主
                if(opreateObject.contains(".")){

                }
                if(tableName.contains(".")){
                    tableName=tableName.substring(tableName.indexOf(".")+1,tableName.length());
                }
                if ("table".equals(opreateObject)) {
                    String flag = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('pro') and d.tablename ='" + tableName + "'");
                    if ("".equals(flag) && "alter".equals(opreate) && !sql.contains("primary")) {
                        logger.info("操作失败，生产没有这个表 "+"tablename:"+tableName);
                        return false;
                    }
                    if ("1".equals(flag) && "create".equals(opreate)) {
                        logger.info("操作失败，生产或老核心已经有这个表了 "+"tablename:"+tableName);
                        return false;
                    }

                    if (("alter").equals(opreate)) {
                        if (("drop").equals(exeOpreate)) {
                            logger.info("操作失败，单个脚本不允许 drop ");
                            return false;
                        }
                        if (("rename").equals(exeOpreate)) {
                            logger.info("操作失败，单个脚本不允许 rename ");
                            return false;
                        }
                        if ("add".equals(exeOpreate)) {
                            String flag1 = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('pro','old') and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "' ");
                            if ("1".equals(flag1)) {
                                logger.info("操作失败，生产或老核心已有此字段"+"tablename:"+tableName+" columnname:"+columnName);
                              //  return false;
                            }
                        }
                        if (("modify").equals(exeOpreate)) {

                            String oldColumnType = JDBCUtils.getOneValue("select d.columntype from databasetablestructure d where d.environment  in ('pro') and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "' ");
                            if ("".equals(oldColumnType)) {
                                logger.info("操作失败，生产不存在此字段"+"tablename:"+tableName+" columnname:"+columnName);
                                return false;
                            }
                            if (!compareColumnType(oldColumnType, columnType)) {
                                logger.info("操作失败，字段类型校验失败"+"tablename:"+tableName+" columnname:"+columnName);
                                return false;
                            }
                        }

                    }
                }

            }

        }

        return true;
    }

    private static boolean compareColumnType(String oldColumnType, String columnType) {
        oldColumnType = oldColumnType.toUpperCase();
        columnType = columnType.toUpperCase();
        String oldColumnFlag = oldColumnType.substring(0, (oldColumnType.indexOf("(") != -1) ? oldColumnType.indexOf("(") : oldColumnType.length());
        String[] oldColumnLengthStrs = oldColumnType.replace(oldColumnFlag, "").replace("(", "").replace(")", "").split(",");
        String columnFlag = columnType.substring(0, (columnType.indexOf("(") != -1) ? columnType.indexOf("(") : columnType.length());
        String[] columnLengthStrs = columnType.replace(columnFlag, "").replace("(", "").replace(")", "").split(",");

        if (!oldColumnFlag.equals(columnFlag)) {
            logger.info("操作失败，脚本中涉及字段类型变更！");
            return false;
        }


        if (oldColumnLengthStrs != null && columnLengthStrs != null) {

            if (oldColumnLengthStrs.length != columnLengthStrs.length) {
                logger.info("操作失败，脚本字段类型涉及精度变更！");
                return false;
            }

            for (int i=0; i<columnLengthStrs.length;i++) {
              Integer columnLength=Integer.parseInt("".equals(columnLengthStrs[i])?"-1":columnLengthStrs[i]);
              Integer oldColumnLength=Integer.parseInt("".equals(oldColumnLengthStrs[i])?"-1":oldColumnLengthStrs[i]);
              if(columnLength<oldColumnLength){
                  logger.info("操作失败，脚本字段长度不允许改小！");
                  return false;
              }

            }
        }

        return true;

    }




}






