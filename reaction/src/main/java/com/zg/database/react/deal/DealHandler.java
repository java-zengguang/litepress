package com.zg.database.react.deal;

public interface DealHandler {
     String getSemaphoreKey();
     boolean isTimeOut();
     Runnable  getDeal();
     Runnable doError();
}
