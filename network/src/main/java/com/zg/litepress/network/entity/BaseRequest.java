package com.zg.litepress.network.entity;

import org.tinylog.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BaseRequest {
    public String id;
    public Object message; //协议请求报文
    public String state;//状态 0-排队中 1-已发送 2-已响应 3-回调完成
    public Object result;//返回结果

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void block() {
        lock.lock();
        try {
            Logger.debug("阻塞请求"+id);
            condition.await(5,TimeUnit.SECONDS); // 线程进入阻塞状态
            Logger.debug("释放请求"+id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void unblock() {
        lock.lock();
        try {
            condition.signal(); // 唤醒阻塞的线程
        } finally {
            lock.unlock();
        }

    }

}

