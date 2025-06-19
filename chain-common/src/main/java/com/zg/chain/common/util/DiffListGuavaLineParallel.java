package com.zg.chain.common.util;


import com.google.common.collect.Sets;
import com.zg.common.annotation.PrimaryKey;
import org.tinylog.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;


public class DiffListGuavaLineParallel {


    Set<CompareEntity> targetSet = null;
    Set<CompareEntity> sourceSet = null;
    Set<CompareEntity> targetSetPK = null;
    Set<CompareEntity> sourceSetPK = null;

    public DiffListGuavaLineParallel(Collection targetDataSet, Collection sourceDataSet) {

        targetSet = Sets.newHashSet(transCompareEntity(targetDataSet, "ALL"));
        sourceSet = Sets.newHashSet(transCompareEntity(sourceDataSet, "ALL"));
        targetSetPK = Sets.newHashSet(transCompareEntity(targetDataSet, "PK"));
        sourceSetPK = Sets.newHashSet(transCompareEntity(sourceDataSet, "PK"));

    }

    public Collection<Object> compareEntityListI() throws ExecutionException, InterruptedException {


        RecursiveTask<Set<CompareEntity>> recursiveTaskKeyI = new RecursiveTask<Set<CompareEntity>>() {
            @Override
            protected Set<CompareEntity> compute() {
                Set<CompareEntity> sourceDifferenceSet = Sets.newHashSet();
                Sets.difference(sourceSetPK, targetSetPK).copyInto(sourceDifferenceSet); //源有目标没有  insert
                return sourceDifferenceSet;
            }
        };
        recursiveTaskKeyI.fork();
        return transObject(recursiveTaskKeyI.get());
    }


    public Collection<Object> compareEntityListD() throws ExecutionException, InterruptedException {


        RecursiveTask<Set<CompareEntity>> recursiveTaskKeyD = new RecursiveTask<Set<CompareEntity>>() {
            @Override
            protected Set<CompareEntity> compute() {
                Set<CompareEntity> targetDifferenceSet = Sets.newHashSet();
                Sets.difference(targetSetPK, sourceSetPK).copyInto(targetDifferenceSet); //目标有源没有  delete
                return targetDifferenceSet;
            }
        };
        recursiveTaskKeyD.fork();
        return transObject(recursiveTaskKeyD.get());
    }

    public Collection<Object> compareEntityListUOld() throws ExecutionException, InterruptedException {

        //全量对比
        Set<CompareEntity> intersectionSet = Sets.newHashSet();
        Sets.intersection(sourceSet, targetSet).copyInto(intersectionSet); //完全一致，不需要操作

        RecursiveTask<Set<CompareEntity>> recursiveTaskAll = new RecursiveTask<Set<CompareEntity>>() {
            @Override
            protected Set<CompareEntity> compute() {
                Set<CompareEntity> symmetricDifferenceSetS = Sets.newHashSet();
                Sets.difference(sourceSet, intersectionSet).copyInto(symmetricDifferenceSetS); //排除完全一致的 待操作的 insert update  集合
                return symmetricDifferenceSetS;
            }
        };

        RecursiveTask<Set<CompareEntity>> recursiveTaskKey = new RecursiveTask<Set<CompareEntity>>() {
            @Override
            protected Set<CompareEntity> compute() {
                Set<CompareEntity> intersectionSetPKS = Sets.newHashSet();
                Sets.intersection(sourceSetPK, targetSetPK).copyInto(intersectionSetPKS); //主键相同 保留source  新值
                return intersectionSetPKS;
            }
        };

        recursiveTaskAll.fork();
        recursiveTaskKey.fork();
        recursiveTaskAll.join();
        recursiveTaskKey.join();
        Set<CompareEntity> symmetricDifferenceSetS = recursiveTaskAll.get();
        Set<CompareEntity> intersectionSetPKS = recursiveTaskKey.get();

        Set<CompareEntity> intersectionSetPKAndDeffS = Sets.newHashSet();
        Sets.intersection(symmetricDifferenceSetS, Sets.newHashSet(transCompareEntity(transObject(intersectionSetPKS), "ALL"))).copyInto(intersectionSetPKAndDeffS);// 等待操作里的主键相同的就是 update 的，update的新值

        Collection<Object> updateSetNew = transObject(intersectionSetPKAndDeffS);
        return updateSetNew;
    }

    public Collection<Object> compareEntityListUNew() throws ExecutionException, InterruptedException {

        //全量对比
        Set<CompareEntity> intersectionSet = Sets.newHashSet();
        Sets.intersection(sourceSet, targetSet).copyInto(intersectionSet); //完全一致，不需要操作

        RecursiveTask<Set<CompareEntity>> recursiveTaskAll = new RecursiveTask<Set<CompareEntity>>() {
            @Override
            protected Set<CompareEntity> compute() {
                Set<CompareEntity> symmetricDifferenceSetT = Sets.newHashSet();
                Sets.difference(targetSet, intersectionSet).copyInto(symmetricDifferenceSetT); //排除完全一致的 待操作的 insert update  集合 保留原值
                return symmetricDifferenceSetT;
            }
        };

        RecursiveTask<Set<CompareEntity>> recursiveTaskKey = new RecursiveTask<Set<CompareEntity>>() {
            @Override
            protected Set<CompareEntity> compute() {
                Set<CompareEntity> intersectionSetPKT = Sets.newHashSet();
                Sets.intersection(targetSetPK, sourceSetPK).copyInto(intersectionSetPKT); //主键相同 保留target  原值
                return intersectionSetPKT;
            }
        };
        recursiveTaskAll.fork();
        recursiveTaskKey.fork();
        recursiveTaskAll.join();
        recursiveTaskKey.join();
        Set<CompareEntity> symmetricDifferenceSetT = recursiveTaskAll.get();
        Set<CompareEntity> intersectionSetPKT = recursiveTaskKey.get();

        Set<CompareEntity> intersectionSetPKAndDeffT = Sets.newHashSet();
        Sets.intersection(symmetricDifferenceSetT, Sets.newHashSet(transCompareEntity(transObject(intersectionSetPKT), "ALL"))).copyInto(intersectionSetPKAndDeffT);// 等待操作里的主键相同的就是 update 的，update的原值
        Collection<Object> updateSetOld = transObject(intersectionSetPKAndDeffT);  //update old

        return updateSetOld;
    }


    public Map compareEntityList() throws ExecutionException, InterruptedException {
        Map<String, Object> map = new HashMap<>();
        map.put("INSERT", compareEntityListI());
        map.put("DELETE", compareEntityListD());
        map.put("UPDATENEW", compareEntityListUNew());
        map.put("UPDATEOLD", compareEntityListUOld());
        return map;
    }

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
