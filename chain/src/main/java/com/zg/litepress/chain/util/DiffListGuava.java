package com.zg.litepress.chain.util;


import com.google.common.collect.Sets;

import com.zg.litepress.core.annotation.PrimaryKey;
import org.tinylog.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class DiffListGuava {

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

    public Map compareEntityList(Collection targetDataList, Collection sourceDataList) {
        Set<Object> targetDataSet = new HashSet<>();
        targetDataSet.addAll(targetDataList);
        Set<Object> sourceDataSet = new HashSet<>();
        sourceDataSet.addAll(sourceDataList);

        Set<CompareEntity> targetSet = Sets.newHashSet(transCompareEntity(targetDataSet, "ALL"));

        Set<CompareEntity> sourceSet = Sets.newHashSet(transCompareEntity(sourceDataSet, "ALL"));

        Set<CompareEntity> targetSetPK = Sets.newHashSet(transCompareEntity(targetDataSet, "PK"));

        Set<CompareEntity> sourceSetPK = Sets.newHashSet(transCompareEntity(sourceDataSet, "PK"));

        Logger.info("---目标--" + targetDataSet.size() + "---源----" + sourceDataSet.size());

        //全量对比
        Logger.info("====0");
        Set<CompareEntity> intersectionSet = Sets.newHashSet();
        Sets.intersection(sourceSet, targetSet).copyInto(intersectionSet); //完全一致，不需要操作
        Logger.info("====1");

        Set<CompareEntity> symmetricDifferenceSetS = Sets.newHashSet();
        Sets.difference(sourceSet, intersectionSet).copyInto(symmetricDifferenceSetS); //排除完全一致的 待操作的 insert update  集合
        Logger.info("====2");

        Set<CompareEntity> symmetricDifferenceSetT = Sets.newHashSet();
        Sets.difference(targetSet, intersectionSet).copyInto(symmetricDifferenceSetT); //排除完全一致的 待操作的 insert update  集合 保留原值
        Logger.info("====3");
        //主键对比

        Set<CompareEntity> intersectionSetPKS = Sets.newHashSet();
        Sets.intersection(sourceSetPK, targetSetPK).copyInto(intersectionSetPKS); //主键相同 保留source  新值
        Logger.info("====4");

        Set<CompareEntity> intersectionSetPKT = Sets.newHashSet();
        Sets.intersection(targetSetPK, sourceSetPK).copyInto(intersectionSetPKT); //主键相同 保留target  原值
        Logger.info("====5");

        Set<CompareEntity> sourceDifferenceSet = Sets.newHashSet();
        Sets.difference(sourceSetPK, targetSetPK).copyInto(sourceDifferenceSet); //源有目标没有  insert
        Logger.info("====6");

        Set<CompareEntity> targetDifferenceSet = Sets.newHashSet();
        Sets.difference(targetSetPK, sourceSetPK).copyInto(targetDifferenceSet); //目标有源没有  delete
        Logger.info("====7");

        //

        Set<CompareEntity> intersectionSetPKAndDeffS = Sets.newHashSet();
        Sets.intersection(symmetricDifferenceSetS, Sets.newHashSet(transCompareEntity(transObject(intersectionSetPKS), "ALL"))).copyInto(intersectionSetPKAndDeffS);// 等待操作里的主键相同的就是 update 的，update的新值
        Logger.info("====8");

        Set<CompareEntity> intersectionSetPKAndDeffT = Sets.newHashSet();
        Sets.intersection(symmetricDifferenceSetT, Sets.newHashSet(transCompareEntity(transObject(intersectionSetPKT), "ALL"))).copyInto(intersectionSetPKAndDeffT);// 等待操作里的主键相同的就是 update 的，update的原值
        Logger.info("====9");

        //整理集合
        Collection<Object> insertSet = transObject(sourceDifferenceSet);  //insert

        Collection<Object> deleteSet = transObject(targetDifferenceSet);  //delete

        Collection<Object> updateSetNew = transObject(intersectionSetPKAndDeffS);

        Collection<Object> updateSetOld = transObject(intersectionSetPKAndDeffT);  //update old

        Logger.info("insert===" + insertSet.size() + "updateNew" + updateSetNew.size() + "====updateSetOld" + updateSetOld.size() + "==delete==" + deleteSet.size());

        Map map = new ConcurrentHashMap();
        map.put("INSERT", insertSet);
        map.put("UPDATENEW", updateSetNew);
        map.put("UPDATEOLD", updateSetOld);
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
                    Object fieldValueA = getFieldObj(fieldName);
                    Object fieldValueB = compareEntity.getFieldObj(fieldName);
                    if (!Objects.equals(fieldValueA, fieldValueB)) {
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
                    Object fieldValue = getFieldObj(fieldName);
                    hashCode = hashCode + Objects.hashCode(fieldValue);

                } catch (IllegalAccessException e) {
                    Logger.error(e);
                }
            }

            return hashCode;
        }


    }


}
