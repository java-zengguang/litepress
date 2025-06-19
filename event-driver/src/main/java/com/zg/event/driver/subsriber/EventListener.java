package com.zg.event.driver.subsriber;

import com.zg.event.driver.event.BaseEvent;

public interface EventListener {
    //回调message ，事件内容
    void callBack(String eventMessage)  throws Exception ;

    void dealEvent(BaseEvent event) throws Exception;

}
