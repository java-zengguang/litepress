package io.github.java_zengguang.litepress.react.deal;

import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseDealHandler implements DealHandler {
    public String semaphoreKey;
    private Runnable timingDeal;
    private Runnable errorDeal;
    private AtomicLong pollingLimit;

    public BaseDealHandler( String semaphoreKey, Runnable timingDeal,Runnable errorDeal,Long pollingLimit ) {
        this.pollingLimit = new AtomicLong(pollingLimit);
        this.timingDeal = timingDeal;
        this.errorDeal=errorDeal;
        this.semaphoreKey = semaphoreKey;
    }

    @Override
    public Runnable getDeal() {
        return timingDeal;
    }

    @Override
    public boolean isTimeOut() {
        return pollingLimit.decrementAndGet() < 0;
    }

    @Override
    public Runnable doError() {
        return errorDeal;
    }

    @Override
    public String getSemaphoreKey() {
        return semaphoreKey;
    }
}
