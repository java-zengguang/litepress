package com.zg.database.react.selector.impl;

import com.zg.database.react.deal.DealHandler;
import com.zg.database.react.selector.Selector;
import com.zg.database.react.semaphore.SemaphoreManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class SimpleSelector implements Selector {

    private final List<DealHandler> semaphoreList = new LinkedList<>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final SemaphoreManager semaphoreManager;
    private Long checkTime;


    public SimpleSelector(Long checkTime, SemaphoreManager semaphoreManager) {
        this.checkTime = checkTime;
        this.semaphoreManager=semaphoreManager;
        init();
    }

    //检查信号量，如果有信号量满足
    private void checkSemaphore() {
        //筛选出超时的
        List<DealHandler> timeOutDealList = semaphoreList.stream().filter(x -> x.isTimeOut()).toList();
        semaphoreList.removeAll(timeOutDealList);
        timeOutDealList.forEach(x -> {
            executor.submit(x.doError());
        });

        //筛选出可执行的
        List<DealHandler> canDealList = semaphoreList.stream().filter(x -> semaphoreManager.checkSemaphore(x.getSemaphoreKey())).toList();
        semaphoreList.removeAll(canDealList);
        canDealList.forEach(x -> {
            executor.submit(x.getDeal());
        });
    }


    private void init() {
        scheduler.scheduleAtFixedRate(() -> {
            this.checkSemaphore();
        }, 0, checkTime, TimeUnit.MILLISECONDS);
    }

    public void register(DealHandler dealHandler,Integer semaphoreValue) {
        semaphoreList.add(dealHandler);
        semaphoreManager.setSemaphore(dealHandler.getSemaphoreKey(),semaphoreValue);
    }


}
