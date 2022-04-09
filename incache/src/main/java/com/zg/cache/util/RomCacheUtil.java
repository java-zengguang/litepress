package com.zg.cache.util;


import java.util.HashMap;
import java.util.Map;

public class RomCacheUtil {

    public static Map<String, RomCacheInte> map = new HashMap<>();

    public static RomCacheInte getRomCache(String key) {

        return map.get(key);
    }

    public static void coverPut(String key, RomCacheInte romCacheInte) {

        map.put(key, romCacheInte);
    }

    public static void noCoverPut(String key, RomCacheInte romCacheInte) {
        if (map.get(key) != null) {

        } else {
            map.put(key, romCacheInte);
        }
    }

}
