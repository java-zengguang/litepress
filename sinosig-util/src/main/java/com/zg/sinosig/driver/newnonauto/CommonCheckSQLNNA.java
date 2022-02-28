package com.zg.sinosig.driver.newnonauto;

import com.zg.database.util.JDBCUtils;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.check.BaseCheckSQL;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonCheckSQLNNA extends BaseCheckSQL implements SunAutoDriver {

    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if ("new-non-auto".equals(systemFlag)) {
             checkSQL(list);
        }
        return list;
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

    @Override
    public boolean checkCustomSQLRule(SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        String vsql=sinoSigSQLLogEntity.basesql;

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

                        if(true) {

                            String flag = JDBCUtils.getOneValue("select owner from databasetablestructure d where d.environment  in ('pro')  and d.tablename ='" + tableName + "'");
                            flag = flag.toLowerCase();
                            if ("".equals(flag) && "alter".equals(opreate) && !sql.contains("constraint")) {
                                sinoSigSQLLogEntity.setErrormassage("检查未通过:生产没有这个表 " + "tablename:" + tableName);
                                return false;
                            }
                            if (!"".equals(flag) && "create".equals(opreate)) {
                                sinoSigSQLLogEntity.setErrormassage("检查未通过:生产已经有这个表了 " + "tablename:" + tableName);
                                return false;
                            }
                            if (!"".equals(flag) && !"nvpolicy".equals(flag) && !"nvproposal".equals(flag) && !"nvendorsement".equals(flag)) {
                                sinoSigSQLLogEntity.setErrormassage("检查未通过:生产环境这个表归属不是承保系统 " + "tablename:" + tableName + " owner:" + flag);
                                return false;
                            }
                        }
                        if(true) {
                            String flag = JDBCUtils.getOneValue("select 1 from tablerelationship d where 1=1  and d.tablename ='" + tableName + "'");
                            flag = flag.toLowerCase();
                            if ("".equals(flag) && "create".equals(opreate)&& tableName.contains("prp")) {
                                sinoSigSQLLogEntity.setErrormassage("检查未通过:新建套表需先维护套表关系 " + "tablename:" + tableName);
                                return false;
                            }
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
                                String flag1 = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('pro') and d.databasename='" + sinoSigSQLLogEntity.databasename + "' and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "' ");
                                if ("1".equals(flag1)) {
                                    sinoSigSQLLogEntity.setErrormassage("检查未通过:生产已有此字段" + "tablename:" + tableName + " columnname:" + columnName);
                                    return false;
                                }
                                flag1 = JDBCUtils.getOneValue("select 1 from databasetablestructure d where d.environment  in ('old','old_dev') and d.tablename ='" + tableName + "' and d.columnname ='" + columnName + "'  and d.columntype <> '" + columnType + "' ");
                                if ("1".equals(flag1)) {
                                    sinoSigSQLLogEntity.setErrormassage("请注意:老核心已有此字段，且字段类型或长度不一致" + "tablename:" + tableName + " columnname:" + columnName);
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
}
