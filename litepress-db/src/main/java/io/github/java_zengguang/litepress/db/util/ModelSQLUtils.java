package io.github.java_zengguang.litepress.db.util;

import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;

import io.github.java_zengguang.litepress.db.dao.assemble.Assemble;
import io.github.java_zengguang.litepress.db.dao.assemble.SimpleAssemble;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class ModelSQLUtils {




    public static String insert(Object model, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String tableName = DBUtils.getTableNameFromModel(model.getClass());
        return insert(model, tableName, dbType);
    }

    //
    public static String insert(Object model, String tableName, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        List<String> memberList = new ArrayList<>();
        List<String> valuesList = new ArrayList<>();
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
        StringBuilder member = new StringBuilder();
        StringBuilder values = new StringBuilder();
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
        List<String> memberList = new ArrayList<>();
        List<String> valuesList = new ArrayList<>();
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
        StringBuilder member = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i) != null && valuesList.get(i) != null && !"".equals(valuesList.get(i))) {

                member.append(memberList.get(i)).append(",");
                values.append(valuesList.get(i)).append(",");

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
                condition.append(entity.columnLabel).append("=").append(entity.columnValue).append(" and ");
            }
        }
        condition = condition.delete(condition.length() - 4, condition.length());

        return "delete from " + tableName + " where " + condition;
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
        return "delete from " + tableName + " where " + condition;
    }


    public static String updateByPK(Object model, List<String> pkList, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        if (pkList == null || pkList.size() == 0) {
            Logger.error("没有输入主键");
        }
        String sql;
        String condition = " ";
        String tableName = DBUtils.getTableNameFromModel(model.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            if (pkList.containsAll(Arrays.asList(entity.columnValue, entity.columnValue.toLowerCase(), entity.columnValue.toUpperCase()))) {  //主鍵條件
                condition = condition + " and " + entity.fieldName + "=" + entity.columnValue;
            } else {
                if ("1".equals(entity.isNotCommit)) {
                    memberList.add(entity.fieldName);
                    valuesList.add(entity.columnValue);
                }
            }
        }
        //  List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuilder memberValues = new StringBuilder();
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


    public static String deleteByPK(Object o, List<String> pkList, String dbType) throws Exception {
        StringBuffer condition = new StringBuffer();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(o);
        String tableName = "";
        for (MetadataEntity entity : list) {
            tableName = entity.tableName;
            if (pkList.containsAll(Arrays.asList(entity.columnValue, entity.columnValue.toLowerCase(), entity.columnValue.toUpperCase()))) {
                condition.append(entity.columnLabel + "=" + entity.columnValue + " and ");
            }
        }
        condition = condition.delete(condition.length() - 4, condition.length());
        return "delete from " + tableName + " where " + condition;
    }


    public static String updateByPK(Object model, String dbType) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String sql;
        String condition = " ";
        String tableName = DBUtils.getTableNameFromModel(model.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> list = assemble.analysis(model);
        for (MetadataEntity entity : list) {
            if ("1".equals(entity.isPK)) {  //主鍵條件
                condition = condition + " and " + entity.fieldName + "=" + entity.columnValue;

            } else {
                if ("1".equals(entity.isNotCommit)) {
                    memberList.add(entity.fieldName);
                    valuesList.add(entity.columnValue);
                }
            }
        }
        //  List<String> notCommitFields = EntityUtils.getNoCommitFields(model.getClass());
        StringBuilder memberValues = new StringBuilder();
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
        if (condition == null || "".equals(condition)) {
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
        String tableName = DBUtils.getTableNameFromModel(newModel.getClass());
        List<String> memberList = new ArrayList();
        List<String> valuesList = new ArrayList();
        Assemble assemble = new SimpleAssemble(dbType);
        List<MetadataEntity> newList = assemble.analysis(newModel);
        List<MetadataEntity> oldList = assemble.analysis(oldModel);
        for (MetadataEntity newEntity : newList) {
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
        StringBuilder memberValues = new StringBuilder();
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


    public static String update(String dbType, Object model, String... terms) throws IllegalArgumentException, IllegalAccessException, SQLException, InstantiationException {
        String sql;
        String condition = " ";
        for (String term : terms) {
            condition = condition + " and " + term;
        }
        String tableName = DBUtils.getTableNameFromModel(model.getClass());
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
        StringBuilder memberValues = new StringBuilder();
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
        StringBuilder member = new StringBuilder();
        StringBuilder values = new StringBuilder();
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
        StringBuilder member_values = new StringBuilder();

        for (int i = 0; i < member_list.size(); i++) {
            member_values.append(" " + member_list.get(i) + "='" + values_list.get(i) + "',");
        }
        member_values.setCharAt(member_values.length() - 1, ' ');
        sql = "update " + table + " set" + member_values + "where " + condition;

        return sql;
    }








}
