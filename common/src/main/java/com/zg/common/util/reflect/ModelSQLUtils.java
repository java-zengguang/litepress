package com.zg.common.util.reflect;

import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.dao.assemble.Assemble;
import com.zg.common.dao.assemble.SimpleAssemble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class ModelSQLUtils {

    private static final Logger logger = LoggerFactory.getLogger(ModelSQLUtils.class);

    public static void fillSql(Object models) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {

    }


    public static String insert(Object model, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        return insert(model, tableName, dbType);
    }

    //
    public static String insert(Object model, String tableName, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                memberList.add(entity.fieldName);
                valuesList.add(entity.columnValue);
            }
        }

        //   List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        String sql = null;
        StringBuffer member = new StringBuffer();
        StringBuffer values = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null && valuesList.get(i) != null && !"".equals(valuesList.get(i))) {

                member.append(memberList.get(i) + ",");
                values.append(valuesList.get(i) + ",");

            }

        }
        member.deleteCharAt(member.length() - 1);
        values.deleteCharAt(values.length() - 1);
        sql = "insert into " + tableName + " (" + member + ") values (" + values + ")";
        return sql;
    }


    public static String replace(Object model, String tableName, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                memberList.add(entity.fieldName);
                valuesList.add(entity.columnValue);
            }
        }

        //   List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        String sql = null;
        StringBuffer member = new StringBuffer();
        StringBuffer values = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null && valuesList.get(i) != null && !"".equals(valuesList.get(i))) {

                member.append(memberList.get(i) + ",");
                values.append(valuesList.get(i) + ",");

            }

        }
        member.deleteCharAt(member.length() - 1);
        values.deleteCharAt(values.length() - 1);
        sql = "replace  into " + tableName + " (" + member + ") values (" + values + ")";
        return sql;
    }


    public static String delete(Object o, String dbType) throws Exception {
        StringBuffer condition = new StringBuffer();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(o);
        String tableName = "";
        for (MetadataEntity entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                tableName = entity.tableName;
                condition.append(entity.columnLabel + "=" + entity.columnValue + " and ");
            }
        }
        condition = condition.delete(condition.length() - 4, condition.length());
        String sql = "delete from " + tableName + " where " + condition;

        return sql;
    }


    public static String deleteByPK(Object o, String dbType) throws Exception {
        StringBuffer condition = new StringBuffer();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(o);
        String tableName = "";
        for (MetadataEntity entity : list) {
            tableName = entity.tableName;
            if ("1".equals(entity.isPK)) {
                condition.append(entity.columnLabel + "=" + entity.columnValue + " and ");
            }
        }
        condition = condition.delete(condition.length() - 4, condition.length());
        String sql = "delete from " + tableName + " where " + condition;
        return sql;
    }


    public static String updateByPK(Object model,List<String> pkList,String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
       if(pkList==null||pkList.size()==0){
           logger.error("没有输入主键");
       }
        String sql ;
        String condition = " ";
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            if(pkList.containsAll(Arrays.asList(entity.columnValue,entity.columnValue.toLowerCase(),entity.columnValue.toUpperCase())))  {  //主鍵條件
                condition = condition + " and " + entity.fieldName+"="+entity.columnValue;
            }else{
                if("1".equals(entity.isNotCommit)) {
                    memberList.add(entity.fieldName);
                    valuesList.add(entity.columnValue);
                }
            }
        }
        //  List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuffer memberValues = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null  && !"".equals(valuesList.get(i)) ) {
                if( valuesList.get(i) != null) {
                    memberValues.append(" " + memberList.get(i) + "=" + valuesList.get(i) + ",");
                }else{
                    memberValues.append(" " + memberList.get(i) + " = null ,");
                }
            }
        }
        memberValues.setCharAt(memberValues.length() - 1, ' ');
        sql = "update " + tableName + " set " + memberValues + "where 1=1 " + condition;

        return sql;
    }


    public static String deleteByPK(Object o,List<String> pkList, String dbType) throws Exception {
        StringBuffer condition = new StringBuffer();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(o);
        String tableName = "";
        for (MetadataEntity entity : list) {
            tableName = entity.tableName;
            if (pkList.containsAll(Arrays.asList(entity.columnValue,entity.columnValue.toLowerCase(),entity.columnValue.toUpperCase()))) {
                condition.append(entity.columnLabel + "=" + entity.columnValue + " and ");
            }
        }
        condition = condition.delete(condition.length() - 4, condition.length());
        String sql = "delete from " + tableName + " where " + condition;
        return sql;
    }


    public static String updateByPK(Object model,String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String sql ;
        String condition = " ";
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
                if("1".equals(entity.isPK))  {  //主鍵條件
                    condition = condition + " and " + entity.fieldName+"="+entity.columnValue;

                }else{
                    if("1".equals(entity.isNotCommit)) {
                        memberList.add(entity.fieldName);
                        valuesList.add(entity.columnValue);
                    }
                }
        }
        //  List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuffer memberValues = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null  && !"".equals(valuesList.get(i)) ) {
                if( valuesList.get(i) != null) {
                    memberValues.append(" " + memberList.get(i) + "=" + valuesList.get(i) + ",");
                }else{
                    memberValues.append(" " + memberList.get(i) + " = null ,");
                }
            }
        }
        memberValues.setCharAt(memberValues.length() - 1, ' ');
        if(condition==null||"".equals(condition)){
            return "";
        }
        sql = "update " + tableName + " set " + memberValues + "where 1=1 " + condition;

        return sql;
    }


    private static boolean isCompare(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null && s2 != null) {
            return false;
        }
        if (s1 != null && s2 == null) {
            return false;
        }

        return s1.equals(s2);
    }

    public static String updateByPK(Object newModel, Object oldModel, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String sql;
        String condition = " ";
        String tableName = EntityUtils.getTableNameFromModel(newModel.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> newList = assemble.analysis(newModel);
        List<MetadataEntity> oldList = assemble.analysis(oldModel);
        for (int i = 0; i < newList.size(); i++) {
            MetadataEntity newEntity = newList.get(i);
            if ("1".equals(newEntity.isPK)) {  //主鍵條件
                condition = condition + " and " + newEntity.fieldName + "=" + newEntity.columnValue;

            } else {
                MetadataEntity oldEntity = oldList.stream().filter(a -> Objects.equals(a.fieldName, newEntity.fieldName)).collect(Collectors.toList()).get(0);
                if ("1".equals(newEntity.isNotCommit) && !isCompare(oldEntity.columnValue, newEntity.columnValue)) {  //只提交不同的位置
                    memberList.add(newEntity.fieldName);
                    valuesList.add(newEntity.columnValue);
                }
            }
        }
        if (valuesList == null || valuesList.size() == 0) {
            return null;
        }
        //  List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuffer memberValues = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null && !"".equals(valuesList.get(i))) {
                if (valuesList.get(i) != null) {
                    memberValues.append(" " + memberList.get(i) + "=" + valuesList.get(i) + ",");
                } else {
                    memberValues.append(" " + memberList.get(i) + " = null ,");
                }
            }
        }
        memberValues.setCharAt(memberValues.length() - 1, ' ');
        sql = "update " + tableName + " set " + memberValues + "where 1=1 " + condition;

        return sql;
    }

/*

    public static String updateByPK(Object model, String dbType) throws Exception {
        String sql;
        String condition = " ";

        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        List<String> pkList = EntityUtils.getPKFields(model.getClass());
        List<String> increaseList = EntityUtils.getIncreaseFields(model.getClass());

        if (pkList == null && pkList.size() == 0) {
            throw new Exception("没有找到主键");
        }

        for (MetadataEntity entity : list) {

            if ("1".equals(entity.isNotCommit) || increaseList.contains(entity.fieldName)) {
                if (!pkList.contains(entity.fieldName)) {
                    memberList.add(entity.fieldName);
                    valuesList.add(entity.columnValue);
                } else {
                    condition = condition + " and " + entity.fieldName + "=" + entity.columnValue;
                }
            }
        }
        //  List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuffer memberValues = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null && !"".equals(valuesList.get(i))) {
                if (valuesList.get(i) != null) {
                    memberValues.append(" " + memberList.get(i) + "=" + valuesList.get(i) + ",");
                } else {
                    memberValues.append(" " + memberList.get(i) + " = null ,");
                }
            }
        }
        memberValues.setCharAt(memberValues.length() - 1, ' ');
        sql = "update " + tableName + " set " + memberValues + "where 1=1 " + condition;

        return sql;
    }
*/


    public static String update(String dbType, Object model, String... terms) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String sql;
        String condition = " ";
        for (String term : terms) {
            condition = condition + " and " + term;
        }
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            if ("1".equals(entity.isNotCommit)) {
                memberList.add(entity.fieldName);
                valuesList.add(entity.columnValue);
            }
        }
        //  List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuffer memberValues = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null && valuesList.get(i) != null && !"".equals(valuesList.get(i))) {
                memberValues.append(" " + memberList.get(i) + "=" + valuesList.get(i) + ",");
            }
        }
        memberValues.setCharAt(memberValues.length() - 1, ' ');
        sql = "update " + tableName + " set " + memberValues + "where 1=1 " + condition;

        return sql;
    }


    public static String insert(String table, List<String> member_list, List values_list) {
        String sql = null;
        StringBuffer member = new StringBuffer();
        StringBuffer values = new StringBuffer();
        for (int i = 0; i < member_list.size(); i++) {
            member.append(member_list.get(i) + ",");
            values.append("'" + values_list.get(i) + "',");
        }
        member.setCharAt(member.length() - 1, ' ');
        values.setCharAt(values.length() - 1, ' ');
        sql = "insert into " + table + " (" + member + ") values (" + values + ")";
        return sql;
    }


    public static String update(String table, List member_list, List values_list, String... terms) {
        String condition = " ";
        for (String term : terms) {
            condition = condition + term + " and ";
        }
        String sql = null;
        StringBuffer member_values = new StringBuffer();

        for (int i = 0; i < member_list.size(); i++) {
            member_values.append(" " + member_list.get(i) + "='" + values_list.get(i) + "',");
        }
        member_values.setCharAt(member_values.length() - 1, ' ');
        sql = "update " + table + " set" + member_values + "where " + condition;

        return sql;
    }


    public static List replaceListSql(List source, List target, String dbType) throws Exception {

        List deleteList = new ArrayList();
        for (Object o : target) {
            o = EntityUtils.translateNull(o);
        }
        for (Object o : source) {
            o = EntityUtils.translateNull(o);
        }

        for (Object o : target) {
            if (!EntityUtils.contains(o, source)) {
                deleteList.add(o);
            }
        }
        List addList = new ArrayList();
        for (Object o : source) {
            if (!EntityUtils.contains(o, target)) {
                addList.add(o);
            }
        }
        List<String> sqlList = new ArrayList<String>();
        for (Object o : deleteList) {
            String sql = ModelSQLUtils.delete(o, dbType);
            sqlList.add(sql);
        }
        for (Object o : addList) {
            String sql = ModelSQLUtils.insert(o, dbType);
            sqlList.add(sql);
        }

        logger.info("SQLList   " + sqlList);

        return sqlList;


    }

    private static boolean isTrue(String string, Object object) {
        return true;
    }

    private static synchronized String resovleSQL(String sql, String key, Object object) throws NoSuchFieldException, IllegalAccessException {

        if (sql.contains(key + "{")) {
            String bracket = sql.substring(sql.indexOf(key + "{") + key.length() + 1, sql.indexOf("}", sql.indexOf(key + "{")));
            if ("$if".equals(key)) {
                if (isTrue(bracket, object)) {
                    sql = sql.replace("$if{" + bracket + "}", bracket);
                } else {
                    sql = sql.replace("$if{" + bracket + "}", "");
                }
            } else if ("#".equals(key)) {
                if (object instanceof Map) {
                    Map<String, String> map = (Map) object;
                    sql = sql.replace("#{" + bracket + "}", map.get(bracket.trim()));
                } else {
                    Field field = object.getClass().getField(bracket.trim());
                    sql = sql.replace("#{" + bracket + "}", EntityUtils.getFieldObject(field, object));
                }
            }
            return sql = resovleSQL(sql, key, object);   //递归执行
        } else {
            return sql;
        }

    }

    public static String dynamicSQL(String sql, Object object) throws NoSuchFieldException, IllegalAccessException {
        sql = resovleSQL(sql, "#", object);
        sql = resovleSQL(sql, "$if", object);

        return sql;
    }

}
