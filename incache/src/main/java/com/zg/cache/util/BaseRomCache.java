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
public  abstract class BaseRomCache implements RomCacheInte {

    //public Object model;
    public Class modelClass;
    public List modelList = new ArrayList();
    public List bankList = new ArrayList();
    public String sql="";

    public BaseRomCache(Class modelClass,String sql){
        this.modelClass=modelClass;
        this.sql=sql;
    }


		/*根据查询条件更新*/

    private String[] getValues(String valueS) {

        String sub[] = valueS.split(":");
        for (int i = 0; i < sub.length; i++) {
            sub[i] = sub[i].trim();
        }
        return sub;
    }


    public boolean updataList(String valueS, int index) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        String[] values = getValues(valueS);
        Field fields[] = modelClass.getFields();
        if (values.length == fields.length) {
            int i = 0;
           Object model =modelClass.newInstance();
            for (Field f : fields) {
                FieldUtils.setField(f, model, values[i].trim());
                i++;
            }
            modelList.set(index, model);
        } else {
            //System.out.println(message.get("error_print_1"));
        }
        return true;
    }

    List appendModelList(List list) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        modelList.addAll(list);
        return modelList;
    }


    /*删除数据*/
    public boolean deleteModel(String... terms) {
        List num_list;
        try {
            num_list = findModelIndex(terms);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        if (num_list.size() > 0) {

            ListIterator it = num_list.listIterator(num_list.size());
            int i = 0;
            while (it.hasPrevious()) {
                i = (int) it.previous();
                modelList.remove(i);
                //	System.out.println(message.get("delete_print_1")+i);
            }
        }
        return true;
    }


    public boolean toOrder(String... terms) {
        for (int i = 0; i < modelList.size(); i++) {
            for (int j = 0; j < modelList.size() - i - 1; j++) {
                try {
                    if (FieldUtils.toCompare(modelList.get(j), modelList.get(j + 1), terms) > 0) {
                        Object copyJ = modelList.get(j);
                        modelList.set(j, modelList.get(j + 1));
                        modelList.set(j + 1, copyJ);
                    }
                } catch (NoSuchFieldException | SecurityException
                        | IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }

            }
        }
        return true;

    }

    public boolean toSimple(String primayKey) {
        Set modelSet = new HashSet(modelList);
        List model_list = new ArrayList(modelSet);
        return true;
    }

    List<Integer> findModelIndex(String... terms) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List num_list = new ArrayList();
        Map termMap = new HashMap();
        for (String term : terms) {
            String[] subS = term.split("=");
            termMap.put(subS[0].trim(), subS[1].trim());

        }
        Iterator it = modelList.iterator();
        for (int i = 0; i < modelList.size(); i++) {

            Object modelX = modelList.get(i);
            if (FieldUtils.toCompare(modelX, termMap) == 0) {
                num_list.add(i);

            }
        }
        return num_list;
    }

    public List findModel(String... terms) {
        List sub_list = new ArrayList();
        List num_list = new ArrayList();
        try {
            num_list = findModelIndex(terms);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {

            e.printStackTrace();

        }
        Iterator it = num_list.iterator();
        while (it.hasNext()) {

            sub_list.add(modelList.get((int) it.next()));
        }
        return sub_list;
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
    public List getList() {
        return modelList;
    }

    public boolean updateListIndex(Object model, int index) {
        modelList.set(index, model);
        return true;
    }

    @Override
    public boolean updateList(Object model, String... terms) {
        try {
            List<Integer> num_list = findModelIndex(terms);
            for (int num : num_list) {
                modelList.set(num, model);
            }

        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean appendList(List list) {
        modelList.addAll(list);
        return true;
    }

    @Override
    public boolean submit() {
        JDBCUtils.commit();
        System.out.println(BaseRomCache.class+"====提交成功，重置链接");
        return true;
    }

    public boolean deleteModel(int index) {

        modelList.remove(index);
        return true;
    }

}
