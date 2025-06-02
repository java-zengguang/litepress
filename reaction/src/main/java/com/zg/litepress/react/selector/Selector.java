package com.zg.litepress.react.selector;

import com.zg.litepress.react.deal.DealHandler;

public interface Selector {
    void register(DealHandler dealHandler,Integer semaphoreValue);
}
