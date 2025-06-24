package io.github.java_zengguang.litepress.react;

import io.github.java_zengguang.litepress.react.deal.impl.SimpleDealHandler;
import io.github.java_zengguang.litepress.react.selector.impl.SimpleSelector;
import io.github.java_zengguang.litepress.react.semaphore.impl.LocalSemaphoreManager;
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