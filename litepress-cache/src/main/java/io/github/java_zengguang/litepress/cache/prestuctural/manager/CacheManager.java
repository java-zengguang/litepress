package io.github.java_zengguang.litepress.cache.prestuctural.manager;

import io.github.java_zengguang.litepress.cache.prestuctural.entity.CacheEntity;

import java.util.*;

public class CacheManager {
    private static final Map<String, CacheEntity> map = new HashMap<>();


    static {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {

            public synchronized void clearDate() {
                long currentTime = System.currentTimeMillis();
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    CacheEntity cacheEntity = map.get(key);
                    if (cacheEntity == null || cacheEntity.validMillisecond + cacheEntity.timeStamp < currentTime) {
                        map.remove(key);
                    }
                }
            }

            public void run() {
                clearDate();
            }

        };

        timer.scheduleAtFixedRate(task, new Date(), 2000);//当前时间开始起动 每次间隔2秒再启动

    }


    public static synchronized void put(String key, Object value, long validMillisecond) {
        CacheEntity cacheEntity = new CacheEntity(value, validMillisecond);
        map.put(key, cacheEntity);
    }


    public static synchronized Object get(String key) {
        CacheEntity cacheEntity = map.get(key);
        if (cacheEntity != null) {
            Object json = cacheEntity.getJsonObject();
            if (json != null) {
                return json;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void remove(String key) {
        map.remove(key);
    }


}
