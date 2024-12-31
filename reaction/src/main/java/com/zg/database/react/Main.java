package com.zg.database.react;

import com.zg.database.react.deal.impl.SimpleDealHandler;
import com.zg.database.react.selector.impl.SimpleSelector;
import com.zg.database.react.semaphore.impl.LocalSemaphoreManager;
import org.tinylog.Logger;


public class Main {
    public static void main(String[] args) {
        LocalSemaphoreManager localSemaphoreManager=   new LocalSemaphoreManager();
        SimpleSelector simpleReaction = new SimpleSelector(1000L,localSemaphoreManager);
        simpleReaction.register(new SimpleDealHandler("xx",
                () ->  Logger.info("获取到信号量"),
                () -> Logger.info("获取信号量超时"), 2L),2);


        Logger.info("----");
        localSemaphoreManager.decrementSemaphore("xx");
    }
}