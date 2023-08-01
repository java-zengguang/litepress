package com.zg.chain.common.util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Check2List {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public static List<Map> mergeList(List list1, List list2) throws IllegalAccessException {
        List<Map> list = new ArrayList<>();
        for (Object o1 : list1) {
            for (Object o2 : list2) {
                Map map = new HashMap();
                Class o1Class = o1.getClass();
                Field[] o1Fields = o1Class.getFields();
                for (Field o1Field : o1Fields) {
                    map.put("o1." + o1Field.getName(), o1Field.get(o1));
                }
                Class o2Class = o2.getClass();
                Field[] o2Fields = o2Class.getFields();
                for (Field o2Field : o2Fields) {
                    map.put("o2." + o2Field.getName(), o2Field.get(o2));
                }
                list.add(map);
            }
        }

        return list;
    }

    public static List<Map> selectList(List<Map> sourceList, WhereOperation... wehres) throws NoSuchFieldException, IllegalAccessException {

        for (WhereOperation whereOperation : wehres) {
            sourceList = whereOperation.compare(sourceList);
        }

        return sourceList;

    }

    public static void main(String args[]) throws IllegalAccessException, NoSuchFieldException {
    }

    public static class WhereOperation {
        public String operation;
        public String target;
        public String value;

        public WhereOperation(String operation, String target, String value) {
            this.operation = operation;
            this.target = target;
            this.value = value;
        }

        private boolean compare(Object obj1, Object obj2) throws NoSuchFieldException, IllegalAccessException {
            if (!(obj1.getClass().getName()).equals(obj2.getClass().getName())) {
                return false;
            }
            if ("=".equals(operation)) {
                if (obj1 instanceof String) {
                    obj1 = ((String) obj1).toUpperCase();
                    obj2 = ((String) obj2).toUpperCase();
                    return obj1.equals(obj2);
                }
            }
            return false;
        }

        public List<Map> compare(List<Map> list) throws NoSuchFieldException, IllegalAccessException {
            List<Map> resultList = new ArrayList<>();

            for (Map<String, Object> map : list) {
                Object obj1 = null;
                Object obj2 = null;
                if (target.contains(".")) {
                    obj1 = map.get(target);
                } else {
                    obj1 = map.get(target);
                }
                if (value.contains(".")) {
                    obj2 = map.get(value);
                } else {
                    obj2 = value;
                }

                if (compare(obj1, obj2)) {
                    resultList.add(map);
                }

            }

            return resultList;

        }

    }
}
