package com.zg.chain.common.util;



import com.zg.common.util.reflect.EntityListUtils;
import com.zg.common.util.reflect.TransEntityTypeUtils;
import org.tinylog.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DiffEntityMap {

    //比较器，用PK比较，用作PK排序，处理主键排序

    //得到主键相同，数据不同的Map key:旧  value:新
    public Map deffEntityMap(Collection newCol, Collection oldCol) throws IllegalAccessException {


        Map<String, Object> newDataMap = EntityListUtils.transToPKMap(newCol);
        Map<String, Object> oldDataMap = EntityListUtils.transToPKMap(oldCol);
        Map<Object, Object> updateMap = new HashMap<>(); //新老数据对

        Set<Map.Entry<String, Object>> entrySet = oldDataMap.entrySet();


        if (newDataMap != null && oldDataMap != null && oldDataMap.size() == newDataMap.size()) {
            for (Map.Entry<String, Object> entry : entrySet) {
                updateMap.put(entry.getValue(), newDataMap.get(entry.getKey()));
            }
        } else {
            new Exception("对比错误，update新老条数不一致");
        }

        Logger.info("updateNew" + newDataMap.size() + "====updateSetOld" + oldDataMap.size());

        Map map = new ConcurrentHashMap();

        map.put("UPDATEMAP", updateMap);

        return map;
    }


}
