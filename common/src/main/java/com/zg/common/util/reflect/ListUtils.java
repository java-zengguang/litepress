package com.zg.common.util.reflect;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Administrator on 2018/11/28 0028.
 */
public class ListUtils {

    //将tableList中的任意两列作为一个map，其中keyLine行具有唯一性
    public static Map createMap(List<Map> tableList, String keyLine, String valueLine) {
        Map resultMap = new HashMap();
        for (Map map : tableList) {
            resultMap.put(map.get(keyLine), map.get(valueLine));
        }
        return resultMap;
    }

    /*根据查询条件更新*/

    private static String[] getValues(String valueS) {

        String sub[] = valueS.split(":");
        for (int i = 0; i < sub.length; i++) {
            sub[i] = sub[i].trim();
        }
        return sub;
    }


    public static boolean updataList(String valueS, int index, Object model, List modelList) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        String[] values = getValues(valueS);
        Field fields[] = model.getClass().getFields();
        if (values.length == fields.length) {
            int i = 0;
            model = model.getClass().newInstance();
            for (Field f : fields) {
                EntityUtils.setField(f, model, values[i].trim());
                i++;
            }
            modelList.set(index, model);
        } else {
            //Logger.info(message.get("error_print_1"));
        }
        return true;
    }


    /*删除数据*/
    public static boolean deleteModel(Class classes, List modelList, String... terms) {
        List num_list;
        try {
            num_list = findModelIndex(classes, modelList, terms);
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
                //	Logger.info(message.get("delete_print_1")+i);
            }
        }
        return true;
    }


    public static boolean toOrder(List modelList, String... terms) {
        for (int i = 0; i < modelList.size(); i++) {
            for (int j = 0; j < modelList.size() - i - 1; j++) {
                try {
                    if (EntityUtils.toCompare(modelList.get(j), modelList.get(j + 1), terms) > 0) {
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

    public static boolean toSimple(List modelList, String primayKey) {
        Set modelSet = new HashSet(modelList);
        List model_list = new ArrayList(modelSet);
        return true;
    }

    public static List<Integer> findModelIndex(Class modelClass, List modelList, String... terms) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List num_list = new ArrayList();
        Map termMap = new HashMap();
        for (String term : terms) {
            String[] subS = term.split("=");
            termMap.put(subS[0].trim(), subS[1].trim());

        }
        Iterator it = modelList.iterator();
        for (int i = 0; i < modelList.size(); i++) {

            Object modelX = modelList.get(i);
            if (EntityUtils.toCompare(modelX, termMap) == 0) {
                num_list.add(i);

            }
        }
        return num_list;
    }

    public static List findModel(List modelList, Class classes, String... terms) {
        List sub_list = new ArrayList();
        List num_list = new ArrayList();
        try {
            num_list = findModelIndex(classes, modelList, terms);
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


}
