package com.zg.litepress.event.subsriber;

import com.zg.litepress.event.event.BaseEvent;

public interface EventListener {
    //回调message ，事件内容
    void callBack(String eventMessage)  throws Exception ;

    void dealEvent(BaseEvent event) throws Exception;

}
