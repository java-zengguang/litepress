package com.zg.sinosig.driver.newnonauto;

import com.zg.handler.ProxyUtils;
import com.zg.sinosig.driver.SunAutoDriver;
import com.zg.sinosig.driver.check.VerificationCheck;
import com.zg.util.sinosing.NewJDBCUtil;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureService;
import com.zg.webdemo.service.databasetablestructure.DatabaseTableStrcutureServiceImpl;

import java.util.*;

public class VerificationCheckNNA extends VerificationCheck implements SunAutoDriver {
    DatabaseTableStrcutureService strcutureService = (DatabaseTableStrcutureService) ProxyUtils.getProxyClass(new DatabaseTableStrcutureServiceImpl(), "insertDataBaseTableStructures,reloadDataBaseTableStructures");


    public void saveDataBaseStrucutre() throws Exception {
        //加载表结构到本地Mysql

        String sql = "select\n" +
                "\t(case\n" +
                "\t\twhen a.TABLE_SCHEMA = 'NVPROPOSAL' then '投保单库'\n" +
                "\t\twhen a.TABLE_SCHEMA = 'NVENDORSEMENT' then '批单修改库'\n" +
                "\t\twhen a.TABLE_SCHEMA = 'NVPOLICY' then '保单库'\n" +
                "\t\telse a.TABLE_SCHEMA end ) as \"databaseName\",\n" +
                "\ta.TABLE_SCHEMA as \"owner\",\n" +
                "\t'test' as \"environment\" ,\n" +
                "\ta.TABLE_NAME as \"tableName\" ,\n" +
                "\t(a.data_type||CHARACTER_MAXIMUM_LENGTH||NUMERIC_PRECISION||NUMERIC_SCALE) as \"columnType\",\n" +
                "\ta.COLUMN_NAME as \"columnName\" ,\n" +
                "\t'' as \"nullAble\",\n" +
                "\t'' as \"dataDefault\",\n" +
                "\t'new-non-auto' as \"systemflag\",\n" +
                "\t'' as \"comments\"\n" +
                "from\n" +
                "\tINFORMATION_SCHEMA.COLUMNS a" +
                " where a.TABLE_SCHEMA  in ('NVENDORSEMENT','NVPROPOSAL','NVPOLICY')  ";
        List<DatabaseTableStructureEntity> list1 = jdbcUtil.select(sql, DatabaseTableStructureEntity.class);
        strcutureService.reloadDataBaseTableStructures(list1, "test", "new-non-auto");
    }


    @Override
    public List<SinoSigSQLLogEntity> doExecute(String systemFlag, List<SinoSigSQLLogEntity> list) throws Exception {
        if("new-non-auto".equals(systemFlag)) {
            checkSQL(list);
        }
        return list;
    }
}
