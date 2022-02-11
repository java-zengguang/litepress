package com.zg.sinosig.check;

import com.zg.database.util.JDBCUtils;
import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.*;
import java.util.logging.Logger;

public abstract class BaseCheckSQL implements CheckSQL {

     Logger logger = Logger.getLogger(BaseCheckSQL.class.getName());


    public boolean checkCommonSQLScript(SinoSigSQLLogEntity sinoSigSQLLogEntity){
        String vsql = sinoSigSQLLogEntity.basesql;
        if (vsql == null || vsql.equals("")) {
            sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本不能为空！");
            return false;
        }

        if (true) {

            String sql = vsql.toLowerCase().trim() + "";

            if (sql == null || sql.equals("")) {
                sinoSigSQLLogEntity.setErrormassage("  检查未通过:不能使用外键，建议使用业务逻辑判断来代替外键!!");
                return false;
            }

            //非阻断
            if (sql.contains("database") || sql.contains("revoke") || sql.contains("flush") || sql.contains("truncate") || sql.contains("rename") || sql.contains("view") || sql.contains("function") || sql.contains("procedure") || sql.contains("drop")) {
                sinoSigSQLLogEntity.setErrormassage("请注意:包含数据库敏感关键字(database,revoke,flush,truncate,rename,view,function,procedure,drop)中的一个或多个，为防止误操作，请人工检查!!");
                //   return false;
            }

            if (sql.contains("sequence")) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:创建序列请注意!!");
                return false;
            }

            if (sql.contains("create table") && !(sql.contains("primary") && sql.contains("key"))) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:新创建表没有主键，表必须有主键!!");
                return false;
            }


            if (!sql.endsWith(";")) {
                sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本必须有结束符!!");
                return false;
            }

            if (true) {
                String[] sqlArray = sql.split(";");
                for (int i = 0; i < sqlArray.length - 1; i++) {
                    if (sqlArray[i].trim().length() < 1) {
                        sinoSigSQLLogEntity.setErrormassage("检查未通过:脚本分割检查有误，请检查是否多了结算符!!");
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public abstract boolean checkCustomSQLRule(SinoSigSQLLogEntity sinoSigSQLLogEntity);

    public boolean checkSQLScript(SinoSigSQLLogEntity sinoSigSQLLogEntity) {
        if(checkCommonSQLScript(sinoSigSQLLogEntity)&&checkCustomSQLRule(sinoSigSQLLogEntity)){
            return true;
        }else{
            return false;
        }

    }


    @Override
    public List<SinoSigSQLLogEntity> checkSQL(List<SinoSigSQLLogEntity> list) throws Exception {
        for (SinoSigSQLLogEntity sinoSigSQLLogEntity : list) {
            if("3".equals(sinoSigSQLLogEntity.executestate)) {
                if (checkSQLScript(sinoSigSQLLogEntity) == false) {
                    sinoSigSQLLogEntity.executestate = "-1";
                    continue;
                }else {
                    sinoSigSQLLogEntity.executestate = "3";

                }
            }
        }
        return list;
    }

}
