package com.zg.database.react.reaction;

import com.zg.database.react.deal.DealHandler;

public interface Reaction {
    void register(DealHandler dealHandler,Integer semaphoreValue);
}
