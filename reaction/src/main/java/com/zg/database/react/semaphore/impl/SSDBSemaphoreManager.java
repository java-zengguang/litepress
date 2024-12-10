package com.zg.database.react.semaphore.impl;

import com.zg.database.react.semaphore.SemaphoreManager;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;

public class SSDBSemaphoreManager implements SemaphoreManager {
    private SSDB ssdb;

    public SSDBSemaphoreManager(SSDB ssdb) {
        this.ssdb = ssdb;
    }

    @Override
    public void decrementSemaphore(String semaphoreKey) {
        ssdb.decr(semaphoreKey, 1);
    }

    @Override
    public boolean checkSemaphore(String semaphoreKey) {
        Response response = ssdb.get(semaphoreKey);
        if (!response.notFound() && response.asInt() < 1) {
            return true;
        }
        return false;
    }

    @Override
    public void setSemaphore(String semaphoreKey, Integer semaphoreValue) {
        ssdb.set(semaphoreKey, semaphoreValue);
    }

    @Override
    public void removeSemaphore(String semaphoreKey) {
        ssdb.del(semaphoreKey);
    }
}
