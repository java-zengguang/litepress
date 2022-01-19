package com.zg.webdemo.dao;

import com.zg.database.util.JDBCUtils;
import com.zg.webdemo.entity.LDCode;
import com.zg.webdemo.entity.SinoSingSQLLogEntity;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class LDCodeMapper {
    public void deletePRPTableLDCode() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, ParseException, IOException {
        JDBCUtils.execute("delete from ldcode where codetype='prp_table' ");
    }

    public void insertLDCode(List<LDCode> ldCodes) throws SQLException, IllegalAccessException {
        JDBCUtils.insertTables(ldCodes,LDCode.class);
    }

    public List<LDCode> getLDcodeAll() throws Exception {
      return   JDBCUtils.select("select *from ldcode ",LDCode.class);
    }
}
