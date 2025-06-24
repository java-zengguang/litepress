package io.github.java_zengguang.litepress.react.deal;

public interface DealHandler {
     String getSemaphoreKey();
     boolean isTimeOut();
     Runnable  getDeal();
     Runnable doError();
}
