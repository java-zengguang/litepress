package com.zg.webdemo.dao;

import com.zg.database.util.JDBCUtils;
import com.zg.webdemo.entity.SinoSingSQLLogEntity;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SinoSingSQLLogMapper {
    public SinoSingSQLLogEntity insertSinoSingSQLLog(SinoSingSQLLogEntity sinoSingSQLLogEntity) throws SQLException, IllegalAccessException {
        if(JDBCUtils.insertTable(sinoSingSQLLogEntity)>0) {
            String sql="select @@IDENTITY as id ";
            List<Map> list=JDBCUtils.selectToMapList(sql);
            Map<String,BigInteger> map=list.get(0);
            System.out.println("id="+ map.get("id").intValue());
            Integer id=Integer.valueOf(map.get("id").intValue());
            sinoSingSQLLogEntity.id=id;
        }
        return sinoSingSQLLogEntity;
    }

    public int updateStateSinoSingSQLLog(SinoSingSQLLogEntity sinoSingSQLLogEntity) throws SQLException {
        if(sinoSingSQLLogEntity.errormassage==null){
            sinoSingSQLLogEntity.errormassage="";
        }
        String sql="UPDATE sinosingsqllog SET executestate='"+sinoSingSQLLogEntity.executestate+"',errormassage='"+sinoSingSQLLogEntity.errormassage+"' WHERE id="+sinoSingSQLLogEntity.id;

        return JDBCUtils.operation(sql);
    }
}
