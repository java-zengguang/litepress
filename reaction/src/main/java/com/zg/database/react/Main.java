package com.zg.database.react;

import com.zg.database.react.deal.impl.SimpleDealHandler;
import com.zg.database.react.reaction.impl.SimpleReaction;
import com.zg.database.react.semaphore.impl.LocalSemaphoreManager;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        LocalSemaphoreManager localSemaphoreManager=   new LocalSemaphoreManager();
        SimpleReaction simpleReaction = new SimpleReaction(1000L,localSemaphoreManager);
        simpleReaction.register(new SimpleDealHandler("xx",
                () ->  System.out.println("获取到信号量"),
                () -> System.out.println("获取信号量超时"), 2L),1);


        System.out.println("----");
        localSemaphoreManager.decrementSemaphore("xx");
    }
}