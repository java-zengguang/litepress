package com.zg.mongodb;

import org.apache.commons.collections.map.HashedMap;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2019/1/15 0015.
 */
public class MongoUtils {


    public static Map bsonToMap(Document bson, Set<String> cluments) {
        Map map = new HashedMap();
        for (String clument : cluments) {
            map.put(clument, bson.get(clument));
        }
        return map;
    }

    public static List<Map> bsonToMapList(List<Document> bsonList, Set<String> cluments) {
        List<Map> mapList = new ArrayList();
        for (Document bson : bsonList) {
            Map map = bsonToMap(bson, cluments);
            mapList.add(map);
        }
        return mapList;
    }

    public static List<Map> bsonToMapList(List<Document> bsonList) {
        for (Document bson : bsonList) {
            bson.remove("_id");
        }
        List<Map> mapList = new ArrayList();
        for (Document bson : bsonList) {
            Map map = bsonToMap(bson, bson.keySet());
            mapList.add(map);
        }
        return mapList;
    }

}
