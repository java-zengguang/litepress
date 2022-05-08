package com.zg.common.dao.mongodb;

import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.dao.assemble.Assemble;
import com.zg.common.dao.assemble.SimpleAssemble;
import com.zg.common.util.reflect.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ModelSQLUtils {

    private static final Logger logger = LoggerFactory.getLogger(ModelSQLUtils.class);

    public static void fillSql(Object models) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {

    }


    public static String insert(Object model) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        return insert(model, tableName);
    }

    //
    public static String insert(Object model, String tableName) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble();
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            memberList.add(entity.fieldName);
            valuesList.add(entity.columnValue);
        }

        List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        String sql = null;
        StringBuffer member = new StringBuffer();
        StringBuffer values = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null && valuesList.get(i) != null && !"".equals(valuesList.get(i)) && !notCommitFields.contains(memberList.get(i))) {

                member.append(memberList.get(i) + ",");
                values.append(valuesList.get(i) + ",");

            }

        }
        member.deleteCharAt(member.length() - 1);
        values.deleteCharAt(values.length() - 1);
        sql = "insert into " + tableName + " (" + member + ") values (" + values + ")";
        return sql;
    }





    public static String delete(Object o) throws Exception {
        StringBuffer condition = new StringBuffer();
        Assemble assemble = new SimpleAssemble();
        List<MetadataEntity> list = assemble.analysis(o);
        String tableName="";
        for (MetadataEntity entity : list) {
            tableName=entity.tableName;
            condition.append(entity.columnLabel + "=" + entity.columnValue + " and ");
        }
        condition = condition.delete(condition.length() - 4, condition.length());
        String sql = "delete from " + tableName + " where " + condition;
        logger.info(sql);
        return sql;
    }


    public static String update(Object model, String... terms) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String sql ;
        String condition = " ";
        for (String term : terms) {
            condition = condition + " and " + term;
        }
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble();
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            memberList.add(entity.fieldName);
            valuesList.add(entity.columnValue);
        }
        List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuffer memberValues = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null && valuesList.get(i) != null && !"".equals(valuesList.get(i)) && !notCommitFields.contains(memberList.get(i))) {
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


    public static List replaceListSql(List source, List target) throws Exception {

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
            String sql = ModelSQLUtils.delete(o);
            sqlList.add(sql);
        }
        for (Object o : addList) {
            String sql = ModelSQLUtils.insert(o);
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
