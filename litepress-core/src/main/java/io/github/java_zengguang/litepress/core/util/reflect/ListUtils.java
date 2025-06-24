package io.github.java_zengguang.litepress.core.util.reflect;

import org.tinylog.Logger;

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





}
