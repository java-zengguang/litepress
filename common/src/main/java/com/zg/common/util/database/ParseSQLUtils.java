package com.zg.common.util.database;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.tinylog.Logger;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ParseSQLUtils {


    public static List<Statement> praseSQL(String sqlStr) throws JSQLParserException {
        List<Statement> statements = new ArrayList<>();
        List<String> sqlList = SQLUtils.splitSQL(sqlStr);
        for (String sql : sqlList) {
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Statement statement = parserManager.parse(new StringReader(sql));
            statements.add(statement);
        }

        return statements;
    }


    public static Set<String> praseScriptTableNameList(String sqlStr) throws JSQLParserException {
        Set<String> tableNameList = new HashSet<>();
        List<String> sqlList = SQLUtils.splitSQL(sqlStr);
        for (String sql : sqlList) {
            tableNameList.addAll(praseSQLTableNameList(sql));
        }

        return tableNameList;

    }


    public static Set<String> praseSQLTableNameList(String sql) throws JSQLParserException {
        Set<String> tableNameList = new HashSet<>();
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement = parserManager.parse(new StringReader(sql));
        if (statement instanceof CreateTable) {
            String tableName = ((CreateTable) statement).getTable().getName();
            tableNameList.add(tableName);
        }
        if (statement instanceof Alter) {
            String tableName = ((Alter) statement).getTable().getName();
            tableNameList.add(tableName);
        }
        if (statement instanceof Update) {
            String tableName = ((Update) statement).getTable().getName();
            tableNameList.add(tableName);
        }
        if (statement instanceof Insert) {
            String tableName = ((Insert) statement).getTable().getName();
            tableNameList.add(tableName);
        }
        if (statement instanceof Delete) {
            String tableName = ((Delete) statement).getTable().getName();
            tableNameList.add(tableName);
        }

        if (statement instanceof Select) {
            PlainSelect select = (PlainSelect) ((Select) statement).getSelectBody();
            Table table = (Table) select.getFromItem();
            tableNameList.add(table.getName());
            List<Join> joins = select.getJoins();
            for (Join join : joins) {
                Table joinTable = (Table) join.getRightItem();
                tableNameList.add(joinTable.getName());
            }

        }

        return tableNameList;

    }

    public static List<String> parseSelectMainTable(String sql) throws JSQLParserException {
        List<String> tableNameList = new ArrayList<>();
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement = parserManager.parse(new StringReader(sql));

        if (statement instanceof Select) {
            PlainSelect select = (PlainSelect) ((Select) statement).getSelectBody();
            Table table = (Table) select.getFromItem();
            tableNameList.add(table.getName());
            List<Join> joins = select.getJoins();
            if (joins != null && joins.size() > 0) {
                for (Join join : joins) {
                    if (join.isSimple()) {
                        Table joinTable = (Table) join.getRightItem();
                        tableNameList.add(joinTable.getName());
                    }
                    if (join.isRight()) {  //如果是right 说明主表在后面，调转主表
                        Table joinTable = (Table) join.getRightItem();
                        tableNameList = new ArrayList<>();
                        tableNameList.add(joinTable.getName());
                    }

                }
            }


        }


        return tableNameList;

    }


    public static void main(String args[]) throws JSQLParserException {
        String sql = "select (select *from prptmain where policyno=a.policyno),a.*from prpcmain a right join xx on eee=xxxd where policyno='123' ";
        List<String> tableNameList = parseSelectMainTable(sql);
        tableNameList.forEach(Logger::info);
    }


}






