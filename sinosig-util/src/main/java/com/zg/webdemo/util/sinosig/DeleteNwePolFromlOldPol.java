package com.zg.webdemo.util.sinosig;


import com.zg.handler.ProxyUtils;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;
import org.python.google.common.collect.Sets;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class DeleteNwePolFromlOldPol {

    private DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures");


    /**
     * 将一组数据平均分成n组
     *
     * @param source 要分组的数据源
     * @param n      平均分成n组
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remainder = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


    private List retainAllByGuava(List list1, List list2) {
        Set set1 = new HashSet<>(list1);
        Set set2 = new HashSet<>(list2);
        Sets.SetView intersectionSet = Sets.intersection(set1, set2);
        List intersectionList = new ArrayList(intersectionSet);
        return intersectionList;
    }

    private List retainAllByJDK8(List<String> newPolicy,List<String> oldPolicy){
        List<String> intersection = newPolicy.stream().filter(item -> oldPolicy.contains(item)).collect(toList());
        intersection.parallelStream().forEach(System.out::println);
        return intersection;
    }


    public List<String> loadData(String tableName) throws SQLException {
        //新库保单号
        NewJDBCUtil newJdbcUtil = new NewJDBCUtil("uat_new-non-auto_nvpolicy");
        List<String> newPolicy = newJdbcUtil.selectOneColList(" select distinct policyno from " + tableName + " a ");
        newJdbcUtil.release();
        System.out.println(tableName+"新保单库数据加载完成");
        //老库保单号
        NewJDBCUtil oldOdbcUtil = new NewJDBCUtil("dev_old-non-auto_sunshine");
        List<String> oldPolicy = oldOdbcUtil.selectOneColList(" select distinct policyno from " + tableName + " a ");
        newJdbcUtil.release();
        System.out.println(tableName+"老保单库数据加载完成");

        //对比
        System.out.println(tableName+"-----对比开始----"+newPolicy.size()+"-----"+oldPolicy.size());
        long begin = System.currentTimeMillis();
        List<String> intersection = retainAllByGuava(oldPolicy, newPolicy);
        long end = System.currentTimeMillis();
        System.out.println(tableName+"-----对比结束-----交集个数-----"+intersection.size()+"----对比方法耗时:" + (end - begin));

        return intersection;
    }


    private void doMain() throws SQLException {

        String[] tables = {"prpcplan", "prpcmain", "prpccardriver", "prpcaddress", "prpcbatch", "prpccardevice", "prpccoins", "prpcfee", "prpcguaranty", "prpcinsured", "prpcinsuredartif", "prpcinsurednature", "prpcitemcargo", "prpcitemdevice", "prpcitemhouse", "prpcitemkind", "prpcitemprop", "prpcitemship", "prpclimit", "prpcmainagri", "prpcmainbank", "prpcmaincargo", "prpcmainconstruct", "prpcmaincredit", "prpcmaininvest", "prpcmainliab", "prpcmainloan", "prpcmainprop", "prpcmainsub", "prpcname", "prpcrenewal", "prpcshipdriver", "prpcexchange", "prpccoinsdetail", "prpcmaincasualty", "prpcreinsceded", "prpcsubsidy", "prpctrafficdetail", "prpcdeductibleclaimrate", "prpcriskitem", "prpcmainriskinfo", "prpcinsuredscott", "prpclifetableinfo", "prpcsales", "prpcfamilydetail", "prpcpatentinfo", "prpcmainextras", "prpcrequestfund", "prpcexpense", "prpspecialinfo", "prppmain", "prppcardriver", "prppaddress", "prppbatch", "prppcardevice", "prppcoins", "prppengage", "prppfee", "prppinsured", "prppinsuredartif", "prppinsurednature", "prppitemcargo", "prppitemdevice", "prppitemhouse", "prppitemkind", "prppitemprop", "prppitemship", "prpplimit", "prppmainagri", "prppmainbank", "prppmaincargo", "prppmainconstruct", "prppmaincredit", "prppmaininvest", "prppmainliab", "prppmainloan", "prppmainprop", "prppname", "prppplan", "prpprenewal", "prppshipdriver", "prppexchange", "prppcoinsdetail", "prppmaincasualty", "prppreinsceded", "prppsubsidy", "prpptrafficdetail", "prppdeductibleclaimrate", "prppinsuredscott", "prpplifetableinfo", "prppsales", "prppfamilydetail", "prpppatentinfo", "prppmainextras", "prpprequestfund", "prppexpense", "prpphead"};
      //  String[] tables={"prppfee","prpphead","prpcplan","prpcaddress","prpcfee","prpcitemkind","prpcmainconstruct","prpccoinsdetail"};
        for (String table : tables) {
            List<String> sqlList = new ArrayList<>();
            List<String> policyNoList = loadData(table);
            for (String policyNo : policyNoList) {
                String sql = "delete from " + table + " where policyno ='" + policyNo + "' ";
                sqlList.add(sql);
            }
            if(sqlList!=null&&sqlList.size()>0) {
                NewJDBCUtil jdbcUtil = new NewJDBCUtil("dev_old-non-auto_sunshine");
                System.out.println("数据删除开始");
                try {
                    jdbcUtil.batchSql(sqlList,true);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    jdbcUtil.commit();
                }

                System.out.println("数据删除完成");
            }

        }

    }

    public static void main(String args[]) {
        DeleteNwePolFromlOldPol deleteNwePolFromlOldPol = new DeleteNwePolFromlOldPol();
        try {
            deleteNwePolFromlOldPol.doMain();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
