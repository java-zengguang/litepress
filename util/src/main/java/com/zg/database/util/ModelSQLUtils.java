package com.zg.database.util;

import com.zg.util.reflect.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ModelSQLUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSQLUtils.class);
    private static List<String> member_list = new ArrayList();
    private static List<String> values_list = new ArrayList();

    public static void fillSql(Object models) throws IllegalArgumentException, IllegalAccessException, SQLException {
        member_list.clear();
        values_list.clear();
        Field fields[] = models.getClass().getFields();


        for (Field f : fields) {
            String name = (String) f.getName();

            member_list.add(name);

            f.setAccessible(true);
            values_list.add(EntityUtils.getFieldObject(f, models));
        }

    }

    public static String getSelect(Object model, String... terms) throws SQLException, IllegalAccessException {
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        return getSelect(model, tableName, terms);
    }

    public static String getSelect(Object model, String tableName, String... terms) throws IllegalArgumentException, IllegalAccessException, SQLException {

        fillSql(model);
        String sql = null;
        StringBuffer member = new StringBuffer();
        String condition = " ";
        for (String term : terms) {
            condition = term + " and " + condition;
        }
        for (String b : member_list) {
            member.append(b + ",");
        }

        member.setCharAt(member.length() - 1, ' ');
        sql = "select " + member.toString() + " from " + tableName + " where  1=1  " + condition;
        return sql;
    }


    public static String insert(Object model) throws IllegalArgumentException, IllegalAccessException, SQLException {
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        return insert(model, tableName);
    }

    //
    public static String insert(Object model, String tableName) throws IllegalArgumentException, IllegalAccessException, SQLException {


        fillSql(model);
        List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        String sql = null;
        StringBuffer member = new StringBuffer();
        StringBuffer values = new StringBuffer();
        for (int i = 0; i < member_list.size(); i++) {
            if (member_list.get(i) != null && values_list.get(i) != null && !"".equals(values_list.get(i)) && !notCommitFields.contains(member_list.get(i))) {

                member.append(member_list.get(i) + ",");
                values.append(values_list.get(i) + ",");

            }

        }
        member.deleteCharAt(member.length() - 1);
        values.deleteCharAt(values.length() - 1);
        sql = "insert into " + tableName + " (" + member + ") values (" + values + ")";
        return sql;
    }


    public static String delete(Object model, String... terms) {
        String condition = " ";
        for (String term : terms) {
            condition = condition + term + " and ";
        }
        String table = model.getClass().getSimpleName();
        String sql = null;
        sql = "delete from " + table + " where " + condition;
        return sql;
    }

    public static String delete(String table, String condition) {
        String sql = null;
        sql = "delete from " + table + " where " + condition;
        return sql;
    }


    public static String delete(Object o) throws Exception {
        StringBuffer condition = new StringBuffer();
        Field[] fields = o.getClass().getFields();
        for (Field field : fields) {
            condition.append(field.getName() + "=" + EntityUtils.getFieldObject(field, o) + " and ");
        }
        condition = condition.delete(condition.length() - 4, condition.length());
        String sql = delete(o.getClass().getSimpleName(), condition.toString());
        LOGGER.info(sql);
        return sql;
    }


    public static String update(Object model, String... terms) throws IllegalArgumentException, IllegalAccessException, SQLException {
        String sql = null;


        String condition = " ";
        for (String term : terms) {
            condition = condition + " and " + term;
        }
        String tableName = EntityUtils.getTableNameFromModel(model.getClass());
        fillSql(model);
        List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuffer member_values = new StringBuffer();

        for (int i = 0; i < member_list.size(); i++) {
            if (member_list.get(i) != null && values_list.get(i) != null && !"".equals(values_list.get(i)) && !notCommitFields.contains(member_list.get(i))) {
                member_values.append(" " + member_list.get(i) + "=" + values_list.get(i) + ",");
            }
        }
        member_values.setCharAt(member_values.length() - 1, ' ');
        sql = "update " + tableName + " set " + member_values + "where 1=1 " + condition;


        return sql;
    }


    public static String getSelect(String table, List<String> member_list, String... terms) {
        String condition = " ";
        for (String term : terms) {
            condition = condition + term + " and ";
        }
        String sql = null;
        StringBuffer member = new StringBuffer();
        for (String b : member_list) {
            member.append(b + ",");
        }

        member.setCharAt(member.length() - 1, ' ');
        sql = "select " + member.toString() + " from " + table + " where " + condition;

        return sql;
    }


    //
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

    public static String update(String table, String oldfield, String newfield, String... terms) {
        String condition = " ";
        for (String term : terms) {
            condition = condition + term + " and ";
        }
        String sql = null;
        StringBuffer member_values = new StringBuffer();


        member_values.append(" " + oldfield + "='" + newfield + "'");

        sql = "update " + table + " set" + member_values + " where " + condition;

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

        //

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

        LOGGER.info("SQLList   " + sqlList);

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
