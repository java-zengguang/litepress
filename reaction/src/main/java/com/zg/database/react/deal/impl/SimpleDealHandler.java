package com.zg.database.react.deal.impl;

import com.zg.database.react.deal.BaseDealHandler;

public  class SimpleDealHandler extends BaseDealHandler {


    public SimpleDealHandler(String semaphoreKey, Runnable timingDeal, Runnable errorDeal, Long pollingLimit) {
        super(semaphoreKey, timingDeal, errorDeal, pollingLimit);
    }



}
