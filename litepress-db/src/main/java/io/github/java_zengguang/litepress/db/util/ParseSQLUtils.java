package io.github.java_zengguang.litepress.db.util;


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




}






