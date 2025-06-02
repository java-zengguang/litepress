package com.zg.litepress.react.semaphore;

public interface SemaphoreManager {
    void decrementSemaphore(String semaphoreKey);

    boolean checkSemaphore(String semaphoreKey);

    void setSemaphore(String semaphoreKey,Integer semaphoreValue);
    void removeSemaphore(String semaphoreKey);
}
