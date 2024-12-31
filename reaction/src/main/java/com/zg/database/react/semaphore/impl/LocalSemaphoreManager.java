package com.zg.database.react.semaphore.impl;

import com.zg.database.react.semaphore.SemaphoreManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalSemaphoreManager implements SemaphoreManager {
    private final static Map<String, AtomicInteger> semaphoreMap = new HashMap<>();


    @Override
    public void decrementSemaphore(String semaphoreKey) {
        if (semaphoreMap.containsKey(semaphoreKey)) {
            semaphoreMap.get(semaphoreKey).decrementAndGet();
        }
    }

    @Override
    public boolean checkSemaphore(String semaphoreKey) {

        if (semaphoreMap.containsKey(semaphoreKey) && semaphoreMap.get(semaphoreKey).get() < 1) {
            semaphoreMap.remove(semaphoreKey);
            return true;
        }
        return false;
    }

    @Override
    public void setSemaphore(String semaphoreKey, Integer semaphoreValue) {
        semaphoreMap.put(semaphoreKey, new AtomicInteger(semaphoreValue));
    }

    @Override
    public void removeSemaphore(String semaphoreKey) {
        semaphoreMap.remove(semaphoreKey);
    }
}
