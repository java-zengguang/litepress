package com.zg.litepress.chain.util;


import com.zg.litepress.core.annotation.PrimaryKey;
import org.tinylog.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DiffEntityGuava {

    //得到主键相同，数据不同的Map key:旧  value:新
    public Map deffEntityMap(Collection newCol, Collection oldCol) {

        List<Object> updateSetNew = newCol.stream().sorted(new PKComparator()).toList();  //update new

        List<Object> updateSetOld = oldCol.stream().sorted(new PKComparator()).toList();  //update old

        Map<Object, Object> updateMap = new HashMap<>(); //新老数据对


        if (updateSetNew != null && updateSetOld != null && updateSetOld.size() == updateSetNew.size()) {
            for (int i = 0; i < updateSetOld.size(); i++) {
                updateMap.put(updateSetOld.get(i), updateSetNew.get(i));
            }
        } else {
            new Exception("对比错误，update新老条数不一致");
        }

        Logger.info("updateNew" + updateSetNew.size() + "====updateSetOld" + updateSetOld.size());

        Map map = new ConcurrentHashMap();

        map.put("UPDATEMAP", updateMap);

        return map;
    }

    //比较器，用PK比较，用作PK排序，处理主键排序
    class PKComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {

            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 != null && o2 == null) {
                return 1;
            }
            if (o1 == null && o2 != null) {
                return -1;
            }


            Field[] fields = o1.getClass().getFields();
            List<String> fieldNameList = new ArrayList<>();
            for (Field field : fields) {
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    fieldNameList.add(field.getName());
                }
            }
            int hashCode1 = 0;
            int hashCode2 = 0;
            for (String fieldName : fieldNameList) {
                try {
                    hashCode1 = hashCode1 + Objects.hashCode(getFieldObj(fieldName, o1));
                    hashCode2 = hashCode2 + Objects.hashCode(getFieldObj(fieldName, o2));
                } catch (IllegalAccessException e) {
                    Logger.error(e);
                }
            }
            return hashCode1 - hashCode2;
        }

        public Object getFieldObj(String fieldName, Object obj) throws IllegalAccessException {
            Class classes = obj.getClass();
            try {
                Field field = classes.getField(fieldName);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                return null;
            }

        }
    }


}
