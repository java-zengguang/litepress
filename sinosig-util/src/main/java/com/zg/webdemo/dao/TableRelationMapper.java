package com.zg.webdemo.dao;

import com.zg.database.util.JDBCUtils;
import com.zg.webdemo.entity.LDCode;
import com.zg.webdemo.entity.TableRelationShipEntity;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class TableRelationMapper {
    public void deleteTableRelation() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, ParseException, IOException {
        JDBCUtils.execute("delete from TableRelationShip ");
    }

    public void insertTableRelation(List<TableRelationShipEntity> ldCodes) throws SQLException, IllegalAccessException {
        JDBCUtils.insertTables(ldCodes,TableRelationShipEntity.class);
    }

    public List<TableRelationShipEntity> getTableReationFromLDcode() throws Exception {
      return   JDBCUtils.select("select t1.codecode as 'tablename',t1.flag as 'basetablename',t1.codecname as 'prefix' ,t1.newflag as 'suffix' ,t2.codecname as 'databasename' from (select *from ldcode l where l.codetype ='table_relationship' ) t1,(select *from ldcode l where l.codetype ='prefix') t2 where t2.codecode=t1.codecname and t1.newflag=t2.flag  ",TableRelationShipEntity.class);
    }
}
