package io.github.java_zengguang.litepress.chain.util;


import com.google.common.collect.Sets;
import io.github.java_zengguang.litepress.core.annotation.PrimaryKey;
import org.tinylog.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class DiffListGuavaLine {

    public Collection<CompareEntity> transCompareEntity(Collection<Object> list, String compareRule) {
        List<CompareEntity> compareEntities = new ArrayList<>();
        for (Object o : list) {
            CompareEntity compareEntity = new CompareEntity(o, compareRule);
            compareEntities.add(compareEntity);
        }
        return compareEntities;
    }

    public Collection<Object> transObject(Collection<CompareEntity> compareEntities) {
        Set<Object> set = new HashSet<>();
        for (CompareEntity compareEntity : compareEntities) {
            set.add(compareEntity.obj);
        }
        return set;
    }

    public Map compareEntityList(Collection targetDataSet, Collection sourceDataSet) {


        Set<CompareEntity> targetSet = Sets.newHashSet(transCompareEntity(targetDataSet, "ALL"));

        Set<CompareEntity> sourceSet = Sets.newHashSet(transCompareEntity(sourceDataSet, "ALL"));

        Set<CompareEntity> targetSetPK = Sets.newHashSet(transCompareEntity(targetDataSet, "PK"));

        Set<CompareEntity> sourceSetPK = Sets.newHashSet(transCompareEntity(sourceDataSet, "PK"));

        Logger.info("---target--" + targetDataSet.size() + "---source----" + sourceDataSet.size());

        //全量对比

        Set<CompareEntity> intersectionSet = Sets.newHashSet();
        Sets.intersection(sourceSet, targetSet).copyInto(intersectionSet); //完全一致，不需要操作


        Set<CompareEntity> symmetricDifferenceSetS = Sets.newHashSet();
        Sets.difference(sourceSet, intersectionSet).copyInto(symmetricDifferenceSetS); //排除完全一致的 待操作的 insert update  集合


        //主键对比

        Set<CompareEntity> intersectionSetPKS = Sets.newHashSet();
        Sets.intersection(sourceSetPK, targetSetPK).copyInto(intersectionSetPKS); //主键相同 保留source  新值


        Set<CompareEntity> sourceDifferenceSet = Sets.newHashSet();
        Sets.difference(sourceSetPK, targetSetPK).copyInto(sourceDifferenceSet); //源有目标没有  insert

        Set<CompareEntity> targetDifferenceSet = Sets.newHashSet();
        Sets.difference(targetSetPK, sourceSetPK).copyInto(targetDifferenceSet); //目标有源没有  delete

        Set<CompareEntity> intersectionSetPKAndDeffS = Sets.newHashSet();
        Sets.intersection(symmetricDifferenceSetS, Sets.newHashSet(transCompareEntity(transObject(intersectionSetPKS), "ALL"))).copyInto(intersectionSetPKAndDeffS);// 等待操作里的主键相同的就是 update 的，update的新值


        //整理集合
        Collection<Object> insertSet = transObject(sourceDifferenceSet);  //insert

        Collection<Object> deleteSet = transObject(targetDifferenceSet);  //delete

        Collection<Object> updateSet = transObject(intersectionSetPKAndDeffS);


        Logger.info("insert===" + insertSet.size() + "update" + updateSet.size() + "==delete==" + deleteSet.size());

        Map map = new ConcurrentHashMap();
        map.put("INSERT", insertSet);
        map.put("UPDATE", updateSet);
        map.put("DELETE", deleteSet);

        return map;
    }

    //
    public class CompareEntity {
        public Object obj;

        public List<String> fieldNameList;

        //isPk=true 代表
        public CompareEntity(Object obj, String compareRule) {
            this.obj = obj;
            Field[] fields = obj.getClass().getFields();
            List<String> fieldNameList = new ArrayList<>();
            for (Field field : fields) {
                if ("PK".equals(compareRule)) {
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    if (primaryKey != null) {
                        fieldNameList.add(field.getName());
                    }
                }
                if ("ALL".equals(compareRule)) {
                    fieldNameList.add(field.getName());
                }
            }

            this.fieldNameList = fieldNameList;
        }

        public Object getFieldObj(String fieldName) throws IllegalAccessException {
            Class classes = obj.getClass();
            try {
                Field field = classes.getField(fieldName);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                return null;
            }

        }

        @Override
        public boolean equals(Object o) {

            CompareEntity compareEntity = (CompareEntity) o;
            try {
                for (String fieldName : fieldNameList) {
                    if (!Objects.equals(getFieldObj(fieldName), compareEntity.getFieldObj(fieldName))) {
                        return false;
                    }
                }
            } catch (Exception e) {
                Logger.error(e);
            }


            return true;
        }

        @Override
        public int hashCode() {
            int hashCode = 0;

            for (String fieldName : fieldNameList) {
                try {
                    hashCode = hashCode + Objects.hashCode(getFieldObj(fieldName));
                } catch (IllegalAccessException e) {
                    Logger.error(e);
                }
            }

            return hashCode;
        }


    }

    //比较器，用PK比较，用作PK排序，处理主键排序
    class PKComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
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
