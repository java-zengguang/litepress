package com.zg.webdemo.dao;

import com.zg.database.util.JDBCUtils;
import com.zg.webdemo.entity.LDCode;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class LDCodeMapper {
    public void deletePRPTableLDCode() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, ParseException, IOException {
        JDBCUtils.execute("delete from ldcode where codetype in('prp_table','table_relationship') ");
    }

    public void insertLDCode(List<LDCode> ldCodes) throws SQLException, IllegalAccessException {
        JDBCUtils.insertTables(ldCodes,LDCode.class);
    }

    public List<LDCode> getLDcodeAll() throws Exception {
      return   JDBCUtils.select("select *from ldcode ",LDCode.class);
    }
}
