package com.zg.webdemo.dao;

import com.zg.database.util.JDBCUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SinoSingSQLLogMapper {
    public SinoSigSQLLogEntity insertSinoSingSQLLog(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws SQLException, IllegalAccessException {
        if(JDBCUtils.insertTable(sinoSigSQLLogEntity)>0) {
            String sql="select @@IDENTITY as id ";
            List<Map> list=JDBCUtils.selectToMapList(sql);
            Map<String,BigInteger> map=list.get(0);
            System.out.println("id="+ map.get("id").intValue());
            Integer id=Integer.valueOf(map.get("id").intValue());
            sinoSigSQLLogEntity.id=id;
        }
        return sinoSigSQLLogEntity;
    }

    public int updateStateSinoSingSQLLog(SinoSigSQLLogEntity sinoSigSQLLogEntity) throws SQLException {
        if(sinoSigSQLLogEntity.errormassage==null){
            sinoSigSQLLogEntity.errormassage="";
        }
        String sql="UPDATE sinosigsqllog SET executestate='"+ sinoSigSQLLogEntity.executestate+"',errormassage='"+ sinoSigSQLLogEntity.errormassage+"' WHERE id="+ sinoSigSQLLogEntity.id;

        return JDBCUtils.operation(sql);
    }
}
