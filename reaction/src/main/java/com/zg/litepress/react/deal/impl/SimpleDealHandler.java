package com.zg.litepress.react.deal.impl;

import com.zg.litepress.react.deal.BaseDealHandler;

public  class SimpleDealHandler extends BaseDealHandler {


    public SimpleDealHandler(String semaphoreKey, Runnable timingDeal, Runnable errorDeal, Long pollingLimit) {
        super(semaphoreKey, timingDeal, errorDeal, pollingLimit);
    }



}
