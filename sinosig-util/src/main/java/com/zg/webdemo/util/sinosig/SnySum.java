package com.zg.webdemo.util.sinosig;

import com.zg.handler.ProxyUtils;
import com.zg.sinosig.load.GetDataStructure;
import com.zg.util.io.POIUtils;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class SnySum {
    private DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures");


    private Map createChcekSUMSQL(String sourceEnvironment, String sourceDatabase,String targetEnvironment, String targetDatabase) throws SQLException {
        String policyNo="abcd1234567890";
        Map resultMap=new HashMap();
        List<String> insertList=new ArrayList();
        List<String> deleteList=new ArrayList();
        List<String> checkList=new ArrayList();
        String[] tables={"prpcmain","prpcplan","prpccardriver","prpcaddress","prpcbatch","prpccardevice","prpccoins","prpcengage","prpcfee","prpcguaranty","prpcinsured","prpcinsuredartif","prpcinsurednature","prpcitemcargo","prpcitemdevice","prpcitemhouse","prpcitemkind","prpcitemprop","prpcitemship","prpclimit","prpcmainagri","prpcmainbank","prpcmaincargo","prpcmainconstruct","prpcmaincredit","prpcmaininvest","prpcmainliab","prpcmainloan","prpcmainprop","prpcmainsub","prpcname","prpcrenewal","prpcshipdriver","prpcexchange","prpccoinsdetail","prpcmaincasualty","prpcvaluedeposite","prpcreinsceded","prpcsubsidy","prpcspecial","prpctrafficdetail","prpcdeductibleclaimrate","prpcriskitem","prpcmainriskinfo","prpcinsuredscott","prpclifetableinfo","prpcsales","prpcborrow","prpcitemkinddetail","prpcnewengage","prpcletguadetail","prpchealthmsg","prpcfamilydetail","prpcpatentinfo","prpcmainextras","prpcagentsub","prpcrequestfund","prpcexpense","prpaccoutinfopay","prpcinvoice","prpccoinssale","prpcpackages","prpcustomergroupinfo","prpcrybsalaryrate","prpchealthy","prpspecialinfo","prppmain","prppcardriver","prppaddress","prppbatch","prppcardevice","prppcoins","prppengage","prpphead","prppfee","prppguaranty","prppinsured","prppinsuredartif","prppinsurednature","prppitemcargo","prppitemdevice","prppitemhouse","prppitemkind","prppitemprop","prppitemship","prpplimit","prppmainagri","prppmainbank","prppmaincargo","prppmainconstruct","prppmaincredit","prppmaininvest","prppmainliab","prppmainloan","prppmainprop","prppmainsub","prppname","prppplan","prpprenewal","prppshipdriver","prppexchange","prppcoinsdetail","prppmaincasualty","prppvaluedeposite","prppreinsceded","prppsubsidy","prppspecial","prpptrafficdetail","prppdeductibleclaimrate","prppinsuredscott","prpplifetableinfo","prppsales","prppborrow","prppitemkinddetail","prppnewengage","prppletguadetail","prpphealthmsg","prppfamilydetail","prpppatentinfo","prppmainextras","prppagentsub","prpprequestfund","prppexpense"};
        for(String table:tables){

            List<Map> tableMapList=  strcutureService.getTableNotNullColumn(sourceEnvironment,sourceDatabase,table);
           if (tableMapList!=null&&tableMapList.size()>0){
               String insertSql="insert into abc_table_name   (abc_columns) values (abc_columnvalues) ";
               if("prpcmain".equals(table)){
                   insertSql="insert into prpcmain   (POLICYNO,classcode,riskcode,policysort,businessnature,makecom,comcode,handlercode,operatorcode) values ('abcd1234567890','1','1','1','1','1','1','1','1') ";
               }
               if("prpphead".equals(table)){
                   insertSql=" insert into prpphead   (ENDORSENO,policyno) values ('abcd1234567890','abcd1234567890') ";
               }
               String deleteSql="delete  from  abc_table_name  where abc_where ";
               String checkSql="select 'abc_table_name',(select 1 from  abc_table_name where abc_where) from dual   ";
               String abc_table_name=table;
               String abc_policyno=policyNo;
               String abc_columns="";
               String abc_columnvalues="";
               String abc_where=" 1=1 ";
               for(Map<String, String> map:tableMapList){
                  String columnName= map.get("columnName");
                  String columnType=map.get("columnType");
                  if(columnType!=null&&columnName!=null){
                      String columnValue="";
                      if("policyno".equals(columnName.toLowerCase())){
                          columnValue="'abc_policyno'";
                      } else if("mainpolicyno".equals(columnName.toLowerCase())){
                          columnValue="'abc_policyno'";
                      }
                      else if("proposalno".equals(columnName.toLowerCase())){
                          columnValue="'abc_policyno'";
                      } else if("endorseno".equals(columnName.toLowerCase())){
                          columnValue="'abc_policyno'";
                      } else if("businessno".equals(columnName.toLowerCase())){
                          columnValue="'abc_policyno'";
                      }else {
                          if (columnType.toLowerCase().contains("char")) {
                              columnValue = "'1'";
                          } else if (columnType.toLowerCase().contains("number")) {
                              columnValue = "1";
                          } else if (columnType.toLowerCase().contains("date")) {
                              columnValue = "date '2021-01-01'";
                          }
                      }
                      abc_columns=abc_columns+columnName+",";
                      abc_columnvalues=abc_columnvalues+columnValue+",";
                      abc_where=abc_where+" and "+columnName+" = "+columnValue;
                  }
               }

               if(!abc_where.contains("abc_policyno")){
                   System.out.println(abc_where);
                   System.out.println("主键问题");
                   return null;
               }

        /*       if(!abc_columns.toLowerCase().contains("policyno")){
                   abc_columns=abc_columns+"policyno,";
                   abc_columnvalues=abc_columnvalues+"'abc_policyno',";
               }*/

               if(abc_columns.length()>1){
                   abc_columns=abc_columns.substring(0,abc_columns.length()-1);
               }
               if(abc_columnvalues.length()>1){
                   abc_columnvalues=abc_columnvalues.substring(0,abc_columnvalues.length()-1);
               }

               insertSql=insertSql.replaceAll("abc_table_name",abc_table_name);
               insertSql=insertSql.replaceAll("abc_columns",abc_columns);
               insertSql=insertSql.replaceAll("abc_columnvalues",abc_columnvalues);
               insertSql=insertSql.replaceAll("abc_policyno",abc_policyno);
               insertList.add(insertSql);

               deleteSql=deleteSql.replaceAll("abc_table_name",abc_table_name);
               deleteSql=deleteSql.replaceAll("abc_columns",abc_columns);
               deleteSql=deleteSql.replaceAll("abc_columnvalues",abc_columnvalues);
               deleteSql=deleteSql.replaceAll("abc_where",abc_where);
               deleteSql=deleteSql.replaceAll("abc_policyno",abc_policyno);
               deleteList.add(deleteSql);


               checkSql=checkSql.replaceAll("abc_table_name",abc_table_name);
               checkSql=checkSql.replaceAll("abc_columns",abc_columns);
               checkSql=checkSql.replaceAll("abc_columnvalues",abc_columnvalues);
               checkSql=checkSql.replaceAll("abc_where",abc_where);
               checkSql=checkSql.replaceAll("abc_policyno",abc_policyno);
               checkList.add(checkSql);

           }

        }

        Collections.reverse(deleteList); //逆序删除脚本

        resultMap.put("insert",insertList);
        resultMap.put("check",checkList);
        resultMap.put("delete",deleteList);
        resultMap.put("sourceEnvironment", sourceEnvironment);
        resultMap.put("sourceDatabase", sourceDatabase);
        resultMap.put("targetEnvironment", targetEnvironment);
        resultMap.put("targetDatabase", targetDatabase);

        return resultMap;
    }

    public void doMain(){
        try {
            Map<String,Map> opreateMap=new HashMap<>();
            opreateMap.put("uat新一代汇总情况",createChcekSUMSQL("uat","保单库","uat","汇总库"));
            opreateMap.put("int新一代汇总情况",createChcekSUMSQL("int","保单库","int","汇总库"));
            opreateMap.put("stage新一代汇总情况",createChcekSUMSQL("stage","保单库","stage","汇总库"));
            opreateMap.put("uat老核心汇总情况", createChcekSUMSQL("old_dev","保单库","uat","汇总库"));
            opreateMap.put("int老核心汇总情况", createChcekSUMSQL("old_dev","保单库","int","汇总库"));
            opreateMap.put("stage老核心汇总情况", createChcekSUMSQL("old_dev","保单库","stage","汇总库"));

            //执行校验
            Map<String, List<Map>> resultMap= excuteCheckData(opreateMap);

            POIUtils.writeXLSX(resultMap,new File("D:\\test\\同步汇总检查.xlsx"));

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<Map>> excuteCheckData(Map<String,Map> mapMap) throws InterruptedException, SQLException {
        Map<String, List<Map>> resultMap=new HashMap();
        Set<String> flagSet=mapMap.keySet();
        for (String flag:flagSet) {
            Map map=mapMap.get(flag);


            String sourceEnvironment=(String) map.get("sourceEnvironment");
            String sourceDatabase=(String) map.get("sourceDatabase");
            String targetEnvironment=(String) map.get("targetEnvironment");
            String targetDatabase=(String) map.get("targetDatabase");
            if(true) {
                System.out.println("-------执行脚本insert----start--");
                List<String> insertSqlList = (List<String>) map.get("insert");
                if (insertSqlList != null && insertSqlList.size() > 0) {
                    NewJDBCUtil jdbcUtil = new NewJDBCUtil(GetDataStructure.getDataSource(sourceEnvironment,sourceDatabase,"new-non-auto"));
                    try {
                        jdbcUtil.batchSql(insertSqlList,true);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } finally {
                        jdbcUtil.commit();
                    }
                }

                System.out.println("-------执行脚本insert----end--");

                Thread.sleep(10*1000);
            }

            if(true) {
                System.out.println("-------执行脚本check----start--");
                List<String> checkSqlList = (List<String>) map.get("check");
                NewJDBCUtil jdbcUtil = new NewJDBCUtil(GetDataStructure.getDataSource(targetEnvironment,targetDatabase,"new-non-auto"));
                if (checkSqlList != null && checkSqlList.size() > 0) {
                    String checkSql="";
                    for(String sql:checkSqlList){
                        checkSql=checkSql+sql+" union all \r\n";
                    }
                    checkSql=checkSql.substring(0,checkSql.lastIndexOf("union all"));
                    try {

                        resultMap.put(flag,jdbcUtil.selectToMapList(checkSql));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("-------执行脚本check----end--");
            }


            if(true) {
                System.out.println("-------执行脚本delete----start--");
                List<String> deleteSqlList = (List<String>) map.get("delete");
                if (deleteSqlList != null && deleteSqlList.size() > 0) {
                    NewJDBCUtil jdbcUtil = new NewJDBCUtil(GetDataStructure.getDataSource(targetEnvironment,targetDatabase,"new-non-auto"));
                    System.out.println("数据删除开始");
                    try {
                        jdbcUtil.batchSql(deleteSqlList,true);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } finally {
                        jdbcUtil.commit();
                    }
                }
                System.out.println("-------执行脚本delete---end--");
            }

        }
        return resultMap;
    }

    public static void main(String args[]){

        SnySum snySum=new SnySum();
        snySum.doMain();
    }

}
