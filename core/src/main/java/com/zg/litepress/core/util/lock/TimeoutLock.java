package com.zg.litepress.core.util.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TimeoutLock {
    private final ReentrantLock lock;
    private final Condition condition;

    public TimeoutLock() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public boolean tryLockWithTimeout(long timeout, TimeUnit unit) {
        try {
            return lock.tryLock(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void unlock() {
        lock.unlock();
    }

    public void waitUntilLockAcquired(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            condition.await(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void signalLockAcquired() {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}