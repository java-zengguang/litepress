package com.zg.database.react.selector;

import com.zg.database.react.deal.DealHandler;

public interface Selector {
    void register(DealHandler dealHandler,Integer semaphoreValue);
}
