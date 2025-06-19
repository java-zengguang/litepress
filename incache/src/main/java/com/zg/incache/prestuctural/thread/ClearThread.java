package com.zg.incache.prestuctural.thread;

import com.zg.incache.prestuctural.entity.CacheEntity;

import java.util.Map;
import java.util.Set;

public class ClearThread implements Runnable {

    private volatile Map<String, CacheEntity> map;

    public ClearThread(Map<String, CacheEntity> map) {
        this.map = map;
    }


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

    @Override
    public void run() {
        clearDate();
    }
}
