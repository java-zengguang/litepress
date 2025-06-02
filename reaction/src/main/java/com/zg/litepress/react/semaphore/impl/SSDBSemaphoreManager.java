package com.zg.litepress.react.semaphore.impl;

import com.zg.litepress.react.semaphore.SemaphoreManager;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;

public class SSDBSemaphoreManager implements SemaphoreManager {
    private SSDB ssdb;
    private final Integer expiryTime;


    public SSDBSemaphoreManager(SSDB ssdb,Integer expiryTime) {
        this.ssdb = ssdb;
        this.expiryTime=expiryTime;
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
        ssdb.setx(semaphoreKey, semaphoreValue,expiryTime);
    }

    @Override
    public void removeSemaphore(String semaphoreKey) {
        ssdb.del(semaphoreKey);
    }
}
