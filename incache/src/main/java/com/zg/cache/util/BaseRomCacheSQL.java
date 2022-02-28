package com.zg.cache.util;

import com.zg.bean.entity.ContainModel;
import com.zg.bean.entity.MainModel;
import com.zg.database.util.JDBCUtils;
import com.zg.database.util.ModelSQLUtils;
import com.zg.util.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public  abstract class BaseRomCacheSQL extends BaseRomCache implements RomCacheSQLInte {

    public String sql="";

    public BaseRomCacheSQL(Class modelClass, String sql) {
        super(modelClass);
        this.sql=sql;
    }

    public boolean upLoadDatabase() {
        try {
            List<String> sqlList = new ArrayList<String>();
            Object model=modelClass.newInstance();
            if (model instanceof MainModel) {
                sqlList = ModelSQLUtils.replaceListSql(modelList, bankList);
            } else if (model instanceof ContainModel) {
                Field[] fields = modelClass.getFields();
                for (Field field : fields) {
                    if (FieldUtils.isPrimitive(field.getType())) {
                        List<String> subSqlList = new ArrayList();
                        List<Object> subModelList = new ArrayList<Object>();
                        List<Object> subBankList = new ArrayList<Object>();

                        for (int i = 0; i < modelList.size(); i++) {
                            Object o = field.get(modelList.get(i));

                            subModelList.add(o);
                        }
                        for (int i = 0; i < bankList.size(); i++) {
                            Object o = field.get(bankList.get(i));
                            subBankList.add(o);
                        }
                        subSqlList = ModelSQLUtils.replaceListSql(subModelList, subBankList);
                        subSqlList.addAll(sqlList);
                        sqlList = subSqlList;
                    }
                }
            }
            JDBCUtils.batchSql(sqlList);
            submit();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }


    @Override
    public boolean downLoadDatabese() throws Exception {

        bankList = JDBCUtils.select(sql, modelClass);
        modelList = JDBCUtils.select(sql, modelClass);
        return true;
    }


    @Override
    public boolean submit() {
        JDBCUtils.commit();
        System.out.println(BaseRomCacheSQL.class+"====提交成功，重置链接");
        return true;
    }


}
